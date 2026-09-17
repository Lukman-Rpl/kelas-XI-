<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use App\Models\Categories;
use App\Models\User;
use App\Models\Wallet;
use Illuminate\Http\Request;
use Illuminate\Support\Carbon;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;

class WalletController extends Controller
{
    public function __construct()
    {
        $this->middleware('auth');
    }

    /**
     * Menampilkan seluruh dompet yang dapat diakses oleh user login
     * (Personal Wallet milik user + Group Wallet dari grup yang diikuti)
     */
    public function index(){
    $user = Auth::user();

    // 1. Ambil ID seluruh grup yang diikuti oleh user
    $userGroupIds = $user->groups()->pluck('groups.id');

    // 2. Kueri Wallet: Personal Wallet milik user ATAU Group Wallet dari grup user
    $wallets = Wallet::with(['group'])
        ->where(function ($query) use ($user, $userGroupIds) {
            $query->where(function ($q) use ($user) {
                $q->where('type', 'personal')
                  ->where('user_id', $user->id);
            })
            ->orWhere(function ($q) use ($userGroupIds) {
                $q->where('type', 'group')
                  ->whereIn('group_id', $userGroupIds);
            });
        })
        ->orderBy('type', 'asc') // Menampilkan Personal Wallet terlebih dahulu
        ->latest('updated_at')
        ->paginate(10);

        $walletIds= $wallets->pluck('id');
        
    // 3. Ambil Kategori Khusus Pengeluaran (Expense) milik user atau kategori bawaan sistem (user_id null)
    $categories = Categories::whereIn('wallet_id', $walletIds)
        ->orWhereNull('wallet_id')
        ->orderBy('categories', 'asc')
        ->get();

    // 4. Kirim data $wallets dan $categories ke view
    return view('wallets.index', compact('wallets', 'categories'));
}

    /**
     * Menyimpan dompet baru (Personal Wallet)
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'name'         => 'required|string|max:255',
            'balance'      => 'nullable|numeric|min:0',
            'budget_limit' => 'nullable|numeric|min:0',
        ]); 

        Wallet::create([
            'name'         => $validated['name'],
            'type'         => 'personal',
            'balance'      => $validated['balance'] ?? 0,
            'budget_limit' => $validated['budget_limit'] ?? 0,
            'user_id'      => Auth::id(),
            'group_id'     => null,
            'is_active'    => true,
        ]);

        return redirect()->route('wallets.index')
            ->with('success', 'Dompet berhasil dibuat.');
    }

    /**
     * Menampilkan detail satu dompet beserta riwayat transaksinya (Read-Only)
     */
    public function show($id)
    {
        // Anggota biasa maupun admin grup BISA melihat detail transaksi
        $wallet = $this->authorizeWalletAccess($id);

        $transactions = $wallet->transactions()
            ->with(['user', 'categories'])
            ->latest('date')
            ->paginate(15);

        return view('wallets.show', compact('wallet', 'transactions'));
    }

    public function setBudget(Request $request, $walletId)
{
    $validated = $request->validate([
        'category_id'  => 'required_without:is_total_budget|nullable|exists:categories,id',
        'limit_amount' => 'required|numeric|min:0'
    ]);

    $wallet = Wallet::findOrFail($walletId);

    // 1. SKENARIO: ANGGARAN TOTAL DOMPET
    if (empty($validated['category_id'])) {
        $wallet->update([
            'budget_limit' => $validated['limit_amount']
        ]);

        return redirect()->back()->with('success', 'Anggaran total dompet berhasil diperbarui!');
    }

    // 2. SKENARIO: ANGGARAN KATEGORI PER WALLET
    // Ambil data referensi nama kategori dari template/dropdown
    $selectedCategory = Categories::findOrFail($validated['category_id']);

    // Simpan atau Update berdasarkan kombinasi wallet_id + nama kategori
    Categories::updateOrCreate(
        [
            'wallet_id'  => $wallet->id,                  // Kunci 1: Harus sesuai Wallet saat ini
            'categories' => $selectedCategory->categories // Kunci 2: Nama kategori (misal: "Belanja")
        ],
        [
            'limit_amount' => $validated['limit_amount']  // Nilai yang diisi/diperbarui
        ]
    );

    return redirect()->back()->with('success', 'Anggaran kategori berhasil disimpan!');
}
/**
     * Menampilkan halaman edit detail wallet beserta daftar anggarannya.
     */
    public function edit($id)
    {
        // 1. Ambil data wallet berdasarkan ID
        $wallet = Wallet::findOrFail($id);
    
        // 2. Ambil kategori yang SUDAH terdaftar di wallet ini
        $categories = $wallet->categories; // atau Category::where('wallet_id', $wallet->id)->get();
    
        // 3. Ambil kategori master yang BELUM terhubung/dipilih (agar muncul di dropdown modal)
        // Jika tidak ada filter khusus, ambil semua data kategori master:
        $availableCategories = \App\Models\Categories::all(); 
    
        // 4. KIRIMKAN variabel $availableCategories ke view
        return view('wallets.edit', compact('wallet', 'categories', 'availableCategories'));
    }
    /**
     * Memperbarui data wallet dan limit anggaran kategori.
     */
    public function update(Request $request, $id)
    {
        // 1. Pengecekan otorisasi khusus EDIT
        $wallet = $this->authorizeWalletAdmin($id);

        // 2. Format / Bersihkan input budget_limit dompet jika diinput dengan format ribuan
        if ($request->has('budget_limit') && is_string($request->budget_limit)) {
            $cleanedBudget = str_replace(['.', ','], '', $request->budget_limit);
            $request->merge(['budget_limit' => $cleanedBudget]);
        }

        // Format / Bersihkan input limit_amount pada array categories jika ada
        if ($request->has('categories') && is_array($request->categories)) {
            $cleanedCategories = $request->categories;
            foreach ($cleanedCategories as $catId => $data) {
                if (isset($data['limit_amount']) && is_string($data['limit_amount'])) {
                    $cleanedCategories[$catId]['limit_amount'] = str_replace(['.', ','], '', $data['limit_amount']);
                }
            }
            $request->merge(['categories' => $cleanedCategories]);
        }

        // 3. Validasi Request
        $validated = $request->validate([
            'name'                      => 'required|string|max:255',
            'budget_limit'              => 'nullable|numeric|min:0',
            'categories'                => 'nullable|array',
            'categories.*.limit_amount' => 'nullable|numeric|min:0',
        ]);

        // 4. Update Informasi Dompet Utama
        $wallet->update([
            'name'         => $validated['name'],
            'budget_limit' => array_key_exists('budget_limit', $validated) && $validated['budget_limit'] !== null 
                                ? $validated['budget_limit'] 
                                : null,
        ]);

        // 5. Update Limit Anggaran Per Kategori (jika dikirim dari form)
        if (!empty($validated['categories'])) {
            foreach ($validated['categories'] as $categoryId => $categoryData) {
                Categories::where('id', $categoryId)
                    ->where('wallet_id', $wallet->id)
                    ->update([
                        'limit_amount' => $categoryData['limit_amount'] ?? 0,
                    ]);
            }
        }

        return redirect()->back()
            ->with('success', 'Dompet dan anggaran berhasil diperbarui.');
    }

    /**
     * Menghapus dompet
     * Hanya Pemilik (Personal) atau Admin Grup (Group Wallet)
     */
    public function destroy($id)
    {
        // Pengecekan otorisasi khusus HAPUS
        $wallet = $this->authorizeWalletAdmin($id);

        $wallet->delete();

        return redirect()->route('wallets.index')
            ->with('success', 'Dompet berhasil dihapus.');
    }

    /**
     * Private Helper: Otorisasi akses BACA (Semua anggota grup / Pemilik personal)
     */
    private function authorizeWalletAccess($id): Wallet
    {
        $user = Auth::user();
        $userGroupIds = $user->groups()->pluck('groups.id');

        return Wallet::with(['group', 'user'])
            ->where(function ($query) use ($user, $userGroupIds) {
                $query->where(function ($q) use ($user) {
                    $q->where('type', 'personal')
                      ->where('user_id', $user->id);
                })
                ->orWhere(function ($q) use ($userGroupIds) {
                    $q->where('type', 'group')
                      ->whereIn('group_id', $userGroupIds);
                });
            })
            ->findOrFail($id);
    }

    /**
     * Private Helper: Otorisasi EDIT & HAPUS
     * - Personal Wallet : Hanya pemilik ($wallet->user_id == $user->id)
     * - Group Wallet    : Hanya Admin/Owner Grup ($group->owner_id == $user->id atau role admin di pivot)
     */
    private function authorizeWalletAdmin($id): Wallet
    {
        $user = Auth::user();
        $wallet = Wallet::with('group')->findOrFail($id);

        // 1. Jika Personal Wallet -> Cek apakah milik user login
        if ($wallet->type === 'personal') {
            if ($wallet->user_id !== $user->id) {
                abort(403, 'Anda tidak memiliki akses untuk mengubah dompet personal ini.');
            }
            return $wallet;
        }

        // 2. Jika Group Wallet -> Cek apakah user adalah Admin/Owner dari Group tersebut
        if ($wallet->type === 'group' && $wallet->group) {
            $group = $wallet->group;

            // Opsi A: Cek via kolom owner_id/created_by di tabel groups
            $isGroupOwner = isset($group->owner_id) && $group->owner_id === $user->id;

            // Opsi B: Cek via relasi pivot table group_user (jika ada kolom role/is_admin)
            $isGroupAdmin = $user->groups()
                ->where('groups.id', $group->id)
                ->wherePivot('role', 'admin') // Sesuaikan nama kolom pivot role Anda jika ada
                ->exists();

            if (!$isGroupOwner && !$isGroupAdmin) {
                abort(403, 'Hanya Admin Grup yang dapat mengubah data dompet grup ini.');
            }

            return $wallet;
        }

        abort(403, 'Akses ditolak.');
    }

    // Tambah Anggaran Kategori Baru
public function storeCategory(Request $request, Wallet $wallet)
{
    $request->validate([
        'category_id' => 'required|exists:categories,id',
        'limit_amount' => 'required|numeric|min:0',
    ]);

    // Contoh simpan ke tabel pivot/relasi
    $wallet->categories()->attach($request->category_id, [
        'limit_amount' => $request->limit_amount
    ]);

    return redirect()->back()->with('success', 'Anggaran kategori berhasil ditambahkan.');
}

// Hapus Anggaran Kategori dari Wallet
public function destroyCategory(Wallet $wallet, $categoryId)
{
    // Detach relasi kategori dari wallet
    $wallet->categories()->detach($categoryId);

    return redirect()->back()->with('success', 'Kategori berhasil dihapus dari wallet.');
}
}