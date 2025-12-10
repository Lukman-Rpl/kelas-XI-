<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\User;
use Illuminate\Support\Facades\Hash;

class AuthController extends Controller
{
    /**
     * REGISTER
     */
    public function register(Request $request)
    {
        $validated = $request->validate([
            'nama' => 'required|string|max:255',
            'email' => 'required|string|email|unique:users',
            'password' => 'required|string|min:6',
            'role' => 'required|in:admin,kurikulum,siswa,kepala-sekolah',
            'kelas_id' => 'nullable|required_if:role,siswa|exists:kelas,id',
        ]);

        $user = User::create([
            'nama' => $validated['nama'],
            'email' => $validated['email'],
            'password' => Hash::make($validated['password']),
            'role' => $validated['role'],
            'kelas_id' => $validated['kelas_id'] ?? null,
        ]);

        return response()->json([
            'success' => true,
            'message' => 'User registered successfully!',
            'data' => $user,
        ]);
    }

    /**
     * LOGIN tanpa token
     */
    public function login(Request $request)
    {
        $validated = $request->validate([
            'email' => 'required|string|email',
            'password' => 'required|string',
            'role' => 'required|in:admin,kurikulum,siswa,kepala-sekolah',
        ]);

        $user = User::where('email', $validated['email'])->first();

        if (!$user) {
            return response()->json(['success' => false, 'message' => 'Email tidak ditemukan'], 404);
        }

        if ($user->role !== $validated['role']) {
            return response()->json(['success' => false, 'message' => 'Role tidak sesuai'], 403);
        }

        if (!Hash::check($validated['password'], $user->password)) {
            return response()->json(['success' => false, 'message' => 'Password salah'], 401);
        }

        // Tidak ada token di sini
        return response()->json([
            'success' => true,
            'message' => 'Login berhasil',
            'user' => [
                'id' => $user->id,
                'nama' => $user->nama,
                'email' => $user->email,
                'role' => $user->role,
                'kelas_id' => $user->kelas_id,
            ]
        ]);
    }

    // ==================== USERS ====================
    public function index()
    {
        return response()->json([
            'success' => true,
            'data' => User::all(),
        ]);
    }

    public function show($id)
    {
        $user = User::find($id);

        if (!$user) {
            return response()->json([
                'success' => false,
                'message' => 'User tidak ditemukan',
            ], 404);
        }

        return response()->json([
            'success' => true,
            'data' => $user,
        ]);
    }

    public function update(Request $request, $id)
    {
        $user = User::find($id);

        if (!$user) {
            return response()->json([
                'success' => false,
                'message' => 'User tidak ditemukan',
            ], 404);
        }

        $validated = $request->validate([
            'nama' => 'sometimes|required|string|max:255',
            'email' => 'sometimes|required|string|email|unique:users,email,' . $id,
            'password' => 'sometimes|required|string|min:6',
            'role' => 'sometimes|required|in:admin,kurikulum,siswa,kepala-sekolah',
        ]);

        if (isset($validated['password'])) {
            $validated['password'] = Hash::make($validated['password']);
        }

        $user->update($validated);

        return response()->json([
            'success' => true,
            'message' => 'User berhasil diperbarui',
            'data' => $user,
        ]);
    }

    public function destroy($id)
    {
        $user = User::find($id);

        if (!$user) {
            return response()->json([
                'success' => false,
                'message' => 'User tidak ditemukan',
            ], 404);
        }

        $user->delete();

        return response()->json([
            'success' => true,
            'message' => 'User berhasil dihapus',
        ]);
    }
}
