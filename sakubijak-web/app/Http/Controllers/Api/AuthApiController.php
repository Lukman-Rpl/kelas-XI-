<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Hash;
use App\Models\User;
use Google_Client;
use Illuminate\Support\Str;

class AuthApiController extends Controller
{
    /**
     * REGISTER
     */
    public function register(Request $request)
    {
        $request->validate([
            'name' => 'required|string|max:100',
            'email' => 'required|email|unique:users',
            'password' => 'required|min:6'
        ]);

        // 1. Simpan User (Menyulut booted() di User.php untuk buat wallet otomatis)
        $user = User::create([
            'name' => $request->name,
            'email' => $request->email,
            'password' => Hash::make($request->password),
        ]);

        $token = $user->createToken('android_token')->plainTextToken;

        // 2. Ambil Wallet Perdana yang baru saja dibuat oleh booted()
        $defaultWallet = $user->wallet()->first();

        return response()->json([
            'status' => true,
            'message' => 'Register berhasil',
            'token' => $token,
            'user' => $user,
            'wallet' => $defaultWallet
        ], 201);
    }

    /**
     * LOGIN MANUAL (EMAIL & PASSWORD)
     */
    public function login(Request $request)
    {
        $request->validate([
            'email' => 'required|email',
            'password' => 'required'
        ]);

        if (!Auth::attempt($request->only('email', 'password'))) {
            return response()->json([
                'status' => false,
                'message' => 'Email atau password salah',
                'token' => null,
                'user' => null,
                'wallet' => null
            ], 401);
        }

        /** @var \App\Models\User $user */
        $user = Auth::user();
        $token = $user->createToken('auth_token')->plainTextToken;

        $defaultWallet = $user->wallet()->first();

        return response()->json([
            'status' => true,
            'message' => 'Login berhasil',
            'token' => $token,
            'user' => $user,
            'wallet' => $defaultWallet
        ]);
    }

    /**
     * LOGIN WITH GOOGLE (DITAMBAHKAN)
     */
    public function googleLogin(Request $request)
{
    $request->validate([
        'id_token' => 'required|string'
    ]);

    // 1. Verifikasi ID Token dari Google SDK Android
    $client = new Google_Client(['client_id' => env('GOOGLE_CLIENT_ID')]);
    $payload = $client->verifyIdToken($request->id_token);

    if (!$payload) {
        return response()->json([
            'status' => false,
            'message' => 'Token Google tidak valid atau kadaluwarsa',
            'token' => null,
            'user' => null,
            'wallet' => null
        ], 401);
    }

    $googleId = $payload['sub']; // ID unik dari Google
    $googleEmail = $payload['email'];
    $googleName = $payload['name'] ?? 'Google User';

    // 2. Cari user berdasarkan email
    $user = User::where('email', $googleEmail)->first();

    // 3. Jika User belum ada (Pengguna Baru) -> Buat User Baru
    if (!$user) {
        // Panggilan User::create() ini AKAN MEMUAT booted() di User.php
        // sehingga WALLET DEFAULT OTOMATIS TERBUAT!
        $user = User::create([
            'google_id' => $googleId, // SIMPAN GOOGLE ID DI SINI
            'name' => $googleName,
            'email' => $googleEmail,
            'password' => Hash::make(Str::random(16)), // Password acak
        ]);
    } else {
        // Jika User sudah ada tapi google_id masih null (misal daftar manual sebelumnya)
        if (empty($user->google_id)) {
            $user->google_id = $googleId;
            $user->save();
        }
    }

    // 4. Generate Sanctum Token
    $token = $user->createToken('google_token')->plainTextToken;

    // 5. Ambil Wallet milik User (Wallet baru dibuat atau wallet lama yang sudah ada)
    $defaultWallet = $user->wallet()->first();

    return response()->json([
        'status' => true,
        'message' => 'Login Google berhasil',
        'token' => $token,
        'user' => $user,
        'wallet' => $defaultWallet
    ]);
}

    /**
     * LOGOUT
     */
    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();

        return response()->json([
            'status' => true,
            'message' => 'Logout berhasil'
        ]);
    }

    /**
     * GET USER (Profile)
     */
    public function me(Request $request)
    {
        return response()->json([
            'status' => true,
            'message' => 'Profile berhasil dimuat',
            'user' => $request->user()
        ]);
    }
}