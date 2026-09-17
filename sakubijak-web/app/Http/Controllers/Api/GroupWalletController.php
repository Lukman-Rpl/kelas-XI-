<?php
namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Group;
use App\Models\GroupInvite;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class GroupWalletController extends Controller
{
    /**
     * API: Join Group via Kode Undangan / Link (Digunakan oleh Pop-up Android)
     */
    public function joinGroup(Request $request)
    {
        $request->validate([
            'code' => 'required|string',
        ]);

        $input = trim($request->code);

        // 1. Ekstraksi Kode jika pengguna menempelkan Full URL/Link
        if (filter_var($input, FILTER_VALIDATE_URL)) {
            // Mengambil query string ?code=XXX atau segmen terakhir path
            $parsedUrl = parse_url($input);
            if (isset($parsedUrl['query'])) {
                parse_str($parsedUrl['query'], $queryParams);
                $code = $queryParams['code'] ?? $input;
            } else {
                $code = last(explode('/', $parsedUrl['path']));
            }
        } else {
            $code = $input;
        }

        $code = strtoupper(trim($code));

        // 2. Cari kode di tabel group_invites
        $invite = GroupInvite::with('group.wallet')->where('code', $code)->first();

        if (!$invite) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Kode atau link undangan tidak ditemukan.'
            ], 404);
        }

        // 3. Cek expired_at
        if ($invite->expired_at && now()->greaterThan($invite->expired_at)) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Kode undangan sudah kadaluarsa.'
            ], 410);
        }

        // 4. Cek max_usage
        if ($invite->max_usage && $invite->used_count >= $invite->max_usage) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Batas penggunaan kode undangan telah habis.'
            ], 410);
        }

        $user = Auth::user();
        $group = $invite->group;

        if (!$group) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Grup tidak ditemukan.'
            ], 404);
        }

        // 5. Cek apakah user sudah terdaftar di grup ini
        if ($group->users()->where('users.id', $user->id)->exists()) {
            return response()->json([
                'status'  => 'success',
                'message' => 'Anda sudah menjadi anggota dompet grup ini.',
                'data'    => $group->wallet
            ], 200);
        }

        // 6. Masukkan ke pivot group_user & naikkan used_count
        $group->users()->attach($user->id, [
            'role' => 'member',
        ]);

        $invite->increment('used_count');

        // Refresh model untuk mengambil data wallet grup terbaru
        $group->load('wallet');

        return response()->json([
            'status'  => 'success',
            'message' => 'Berhasil bergabung ke grup ' . $group->name,
            'data'    => $group->wallet
        ], 200);
    }

    /**
     * API: Mendapatkan Kode Undangan Aktif milik Group tertentu
     */
    public function getInviteCode($groupId)
    {
        $user = Auth::user();
        $group = Group::with('invites')->findOrFail($groupId);

        // Otorisasi: Pastikan user adalah anggota grup ini
        if (!$group->users()->where('users.id', $user->id)->exists()) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Akses ditolak.'
            ], 403);
        }

        // Ambil kode undangan aktif pertama (atau buat baru jika belum ada)
        $invite = $group->invites()->latest()->first();

        if (!$invite) {
            $invite = $group->invites()->create([
                'code'       => strtoupper(\Illuminate\Support\Str::random(6)), // Buat kode acak unik
                'expired_at' => null,
                'max_usage'  => null,
            ]);
        }

        return response()->json([
            'status'     => 'success',
            'group_id'   => $group->id,
            'group_name' => $group->name,
            'code'       => $invite->code,
            'share_url'  => 'https://sakubijak.com/join?code=' . $invite->code
        ], 200);
    }
}