<?php
namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use App\Models\Categories;
use App\Models\Transaction;
use App\Models\Wallet;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Validation\ValidationException;

class TransactionController extends Controller
{
    public function __construct()
    {
        $this->middleware('auth');
    }

    /**
     * Menampilkan seluruh transaksi yang relevan bagi user
     * (Transaksi Personal User + Transaksi Anggota Lain di Group yang User Ikuti)
     */
    public function index(Request $request)
    {
        $user = Auth::user();

        // Ambil semua ID grup yang diikuti oleh user
        $userGroupIds = $user->groups()->pluck('groups.id');

        // Kueri Transaksi: Personal milik user ATAU Transaksi yang terjadi di Group-nya
        $query = Transaction::with(['user', 'wallet', 'group', 'category', 'categories'])
            ->where(function ($q) use ($user, $userGroupIds) {
                $q->where('user_id', $user->id)
                  ->orWhereIn('group_id', $userGroupIds);
            });

        // Filter Opsional: Berdasarkan Tanggal Mulai
        if ($request->filled('start_date')) {
            $query->whereDate('date', '>=', $request->start_date);
        }

        // Filter Opsional: Berdasarkan Tanggal Selesai
        if ($request->filled('end_date')) {
            $query->whereDate('date', '<=', $request->end_date);
        }

        // Filter Opsional: Berdasarkan Dompet
        if ($request->filled('wallet_id')) {
            $query->where('wallet_id', $request->wallet_id);
        }

        // Filter Opsional: Berdasarkan Tipe (income / expense)
        if ($request->filled('type') && in_array($request->type, ['income', 'expense'])) {
            $query->where('type', $request->type);
        }

        // Filter Opsional: Pencarian berdasarkan deskripsi, nama penerima, atau nomor rekening
        if ($request->filled('search')) {
            $search = $request->search;
            $query->where(function ($q) use ($search) {
                $q->where('description', 'like', "%{$search}%")
                  ->orWhere('note', 'like', "%{$search}%")
                  ->orWhere('recipient_name', 'like', "%{$search}%")
                  ->orWhere('recipient_account', 'like', "%{$search}%");
            });
        }

        $transactions = $query->latest('date')->latest('created_at')->paginate(15)->withQueryString();

        // Ambil list wallet milik user untuk dropdown pada filter view
        $wallets = Wallet::where('user_id', $user->id)
            ->orWhereIn('group_id', $userGroupIds)
            ->get();
        $categories = Categories::orderBy('categories')->get();

        return view('transactions.index', compact('transactions', 'wallets', 'categories'));
    }

    /**
     * Menampilkan detail satu transaksi tertentu
     */
    public function show($id)
    {
        $user = Auth::user();
        $userGroupIds = $user->groups()->pluck('groups.id');

        $transaction = Transaction::with(['user', 'wallet', 'group', 'category', 'categories'])
            ->where(function ($q) use ($user, $userGroupIds) {
                $q->where('user_id', $user->id)
                  ->orWhereIn('group_id', $userGroupIds);
            })
            ->findOrFail($id);

        return view('transactions.show', compact('transaction'));
    }

    /**
     * Menyimpan transaksi baru dari Web (jika ada form input transaksi di web)
     */
    public function store(Request $request)
{
    $request->validate([
        'wallet_id'         => 'required|exists:wallets,id',
        'category_id'       => 'required|exists:categories,id',
        'group_id'          => 'nullable|exists:groups,id',
        'amount'            => 'required|numeric|min:0.01',
        'type'              => 'required|in:income,expense',
        'date'              => 'required|date',
        'description'       => 'nullable|string|max:255',
        'recipient_account' => 'nullable|string|max:50',
        'recipient_name'    => 'nullable|string|max:255',
    ]);

    return DB::transaction(function () use ($request) {
        // 1. Ambil dompet dengan Lock
        $wallet = Wallet::where('id', $request->wallet_id)
            ->where(function ($query) {
                $query->where('user_id', Auth::id())
                      ->orWhereHas('members', function ($q) {
                          $q->where('user_id', Auth::id());
                      });
            })
            ->lockForUpdate()
            ->firstOrFail();

        // 2. Cek Saldo Dompet
        if ($request->type === 'expense' && $wallet->balance < $request->amount) {
            throw ValidationException::withMessages([
                'amount' => ['Saldo dompet tidak mencukupi untuk transaksi ini.']
            ]);
        }

        // 3. PERBAIKAN (SKENARIO A): Cek Batas Anggaran Per Kategori di Wallet Ini
        if ($request->type === 'expense') {
            // A. Ambil kategori khusus yang terikat dengan wallet ini (atau hilangkan where wallet_id jika ID kategori global tapi unik)
            $category = Categories::where('id', $request->category_id)
                ->where('wallet_id', $wallet->id)
                ->first();

            // Tip: Menggunakan nama model tunggal 'Category' bukan 'Categories'
            if (!$category) {
                $category = Categories::findOrFail($request->category_id);
            }

            // B. Cek apakah kategori ini memiliki batas anggaran (limit_amount > 0)
            if ($category && $category->limit_amount > 0) {
                $transactionDate = Carbon::parse($request->date);
                $startOfMonth    = $transactionDate->copy()->startOfMonth()->toDateString();
                $endOfMonth      = $transactionDate->copy()->endOfMonth()->toDateString();

                // C. Hitung total pengeluaran khusus KHUSUS untuk Wallet ID & Category ID ini pada bulan berjalan
                $currentCategoryExpense = Transaction::where('wallet_id', $wallet->id)
                    ->where('category_id', $category->id)
                    ->where('type', 'expense')
                    ->whereBetween('date', [$startOfMonth, $endOfMonth])
                    ->sum('amount');

                // D. Validasi limit
                if (($currentCategoryExpense + $request->amount) > $category->limit_amount) {
                    throw ValidationException::withMessages([
                        'amount' => ["Transaksi ini melebihi batas anggaran kategori {$category->name} untuk dompet ini."]
                    ]);
                }
            }
        }

        // 4. Update Saldo Wallet
        if ($request->type === 'expense') {
            $wallet->balance -= $request->amount;
        } else {
            $wallet->balance += $request->amount;
        }
        $wallet->save();

        // 5. Simpan Transaksi
        $transaction = Transaction::create([
            'user_id'           => Auth::id(),
            'wallet_id'         => $wallet->id,
            'group_id'          => $request->group_id ?? $wallet->group_id ?? null,
            'category_id'       => $request->category_id,
            'amount'            => $request->amount,
            'type'              => $request->type,
            'date'              => $request->date,
            'description'       => $request->description,
            'recipient_account' => $request->recipient_account,
            'recipient_name'    => $request->recipient_name,
        ]);

        if ($request->wantsJson()) {
            return response()->json([
                'status'  => 'success',
                'message' => 'Transaksi berhasil ditambahkan.',
                'data'    => $transaction
            ], 201);
        }

        return redirect()->route('transactions.index')->with('success', 'Transaksi berhasil ditambahkan.');
    });
}
}