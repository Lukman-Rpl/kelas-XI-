<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use App\Models\Group;
use App\Models\GroupInvite;
use App\Models\Wallet;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;

class GroupController extends Controller
{
    public function __construct()
    {
        $this->middleware('auth');
    }

    /**
     * Daftar grup yang diikuti user
     */
    public function index()
    {
        $userId = Auth::id();

        // Tambahkan 'invites' pada eger loading with()
        $groups = Group::with(['wallet', 'users', 'invites'])
            ->whereHas('users', function ($query) use ($userId) {
                $query->where('users.id', $userId);
            })
            ->latest()
            ->paginate(10);

        return view('groups.index', compact('groups'));
    }
    /**
     * Form tambah grup baru
     */
    public function create()
    {
        return view('groups.create');
    }

    /**
     * Menyimpan Group Baru sekaligus membuat Group Wallet dan Group Invite secara Atomic
     */
    /**
     * Menyimpan Group Baru sekaligus membuat Group Wallet secara Atomic
     */
    public function store(Request $request)
    {
        // Hanya memvalidasi Nama Grup
        $request->validate([
            'name' => 'required|string|max:255',
        ]);

        $owner = Auth::user();

        // Gunakan DB Transaction agar pembuatan Group, Wallet, Pivot, dan Invite berhasil bersamaan
        DB::transaction(function () use ($request, $owner) {
            // 1. Buat Grup
            $group = Group::create([
                'name'     => $request->name,
                'owner_id' => $owner->id,
            ]);

            // 2. Buat Group Wallet (Saldo awal default 0, siap diisi via Midtrans)
            Wallet::create([
                'name'     => 'Dompet ' . $group->name,
                'type'     => 'group',
                'group_id' => $group->id,
                'user_id'  => null,
                'balance'  => 0,
            ]);

            // 3. Masukkan Owner ke pivot group_user sebagai Admin
            $group->users()->attach($owner->id, [
                'role' => 'admin',
            ]);

            // 4. Otomatis buatkan Kode Undangan Baru di tabel group_invites
            $group->invites()->create([
                'expired_at' => null, // Berlaku selamanya
                'max_usage'  => null, // Tanpa batas kuota
            ]);
        });

        return redirect()->route('groups.index')
            ->with('success', 'Grup dan Dompet Grup berhasil dibuat!');
    }

    /**
     * Detail group (Menampilkan Wallet, Transaksi, & Anggota)
     */
    public function show($id)
    {
        $userId = Auth::id();

        $group = Group::with(['wallet', 'users', 'owner', 'invites'])
            ->whereHas('users', function ($query) use ($userId) {
                $query->where('users.id', $userId);
            })
            ->findOrFail($id);

        $transactions = $group->transactions()
            ->with(['user', 'categories'])
            ->latest('date')
            ->paginate(15);

        return view('groups.show', compact('group', 'transactions'));
    }

    /**
     * Daftar anggota group
     */
    public function members($id)
    {
        $userId = Auth::id();

        $group = Group::with(['users' => function ($query) {
            $query->orderBy('group_user.role', 'asc');
        }])
        ->whereHas('users', function ($query) use ($userId) {
            $query->where('users.id', $userId);
        })
        ->findOrFail($id);

        return view('groups.members', compact('group'));
    }

    /**
     * PERUBAHAN DI SINI: Fitur Join Group dengan Kode Undangan Baru
     */
    public function join(Request $request)
    {
        $request->validate([
            'invite_code' => 'required|string',
        ]);

        $code = strtoupper(trim($request->invite_code));

        // 1. Cari kode di tabel group_invites beserta relasi grupnya
        $invite = GroupInvite::with('group')->where('code', $code)->first();

        if (!$invite) {
            return back()->with('error', 'Kode undangan tidak ditemukan.');
        }

        // 2. Cek apakah kode sudah kadaluarsa (expired)
        if ($invite->expired_at && now()->greaterThan($invite->expired_at)) {
            return back()->with('error', 'Kode undangan sudah kadaluarsa.');
        }

        // 3. Cek apakah batas kuota pemakaian sudah habis
        if ($invite->max_usage && $invite->used_count >= $invite->max_usage) {
            return back()->with('error', 'Batas penggunaan kode undangan telah habis.');
        }

        $user = Auth::user();
        $group = $invite->group;

        // 4. Cek apakah user sudah terdaftar di grup ini
        if ($group->users()->where('users.id', $user->id)->exists()) {
            return redirect()->route('groups.show', $group->id)
                ->with('info', 'Anda sudah menjadi anggota grup ini.');
        }

        // 5. Masukkan user ke tabel pivot sebagai 'member' & naikkan hitungan used_count
        $group->users()->attach($user->id, [
            'role' => 'member',
        ]);

        $invite->increment('used_count');

        return redirect()->route('groups.show', $group->id)
            ->with('success', 'Berhasil bergabung dengan grup ' . $group->name);
    }

    /**
     * Mengeluarkan (Kick) Member dari Grup
     */
    public function kickMember(Request $request, $groupId, $userId)
    {
        $currentUser = Auth::user();
        $group = Group::findOrFail($groupId);

        // 1. Validasi: Hanya Admin/Owner Grup yang boleh melakukan kick
        $currentRole = $group->users()->where('users.id', $currentUser->id)->first()?->pivot->role;
        if ($currentRole !== 'admin' && $group->owner_id !== $currentUser->id) {
            abort(403, 'Anda tidak memiliki hak akses untuk mengeluarkan anggota.');
        }

        // 2. Validasi: Owner tidak bisa di-kick
        if ((int)$userId === (int)$group->owner_id) {
            return back()->with('error', 'Owner grup tidak dapat dikeluarkan dari grup.');
        }

        // 3. Validasi: Admin tidak bisa meng-kick dirinya sendiri lewat tombol ini
        if ((int)$userId === (int)$currentUser->id) {
            return back()->with('error', 'Gunakan fitur keluar grup (leave) jika ingin keluar.');
        }

        // Detach (hapus relasi di tabel pivot group_user)
        $group->users()->detach($userId);

        return back()->with('success', 'Anggota berhasil dikeluarkan dari grup.');
    }

    /**
     * Menghapus Group beserta Wallet dan Kode Undangan terkait
     */
    public function destroy($id)
    {
        $currentUser = Auth::user();

        // 1. Cari grup beserta data owner-nya
        $group = Group::findOrFail($id);

        // 2. Otorisasi: Hanya Owner Grup yang berhak menghapus grup secara permanen
        if ((int)$group->owner_id !== (int)$currentUser->id) {
            return back()->with('error', 'Hanya owner grup yang dapat menghapus grup ini.');
        }

        // 3. Eksekusi Hapus dalam DB Transaction
        DB::transaction(function () use ($group) {
            // Hapus dompet grup jika ada
            if ($group->wallet) {
                $group->wallet()->delete();
            }

            // Detach seluruh anggota di tabel pivot group_user
            $group->users()->detach();

            // Hapus grup (kode undangan di group_invites terhapus otomatis via cascadeOnDelete)
            $group->delete();
        });

        return redirect()->route('groups.index')
            ->with('success', 'Grup berhasil dihapus.');
    }
}