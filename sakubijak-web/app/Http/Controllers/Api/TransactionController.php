<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Categories;
use App\Models\Transaction;
use App\Models\Wallet;
use Carbon\Carbon;
use Exception;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Validation\ValidationException;
use Midtrans\Config;
use Midtrans\Snap;

class TransactionController extends Controller
{
    /**
     * Tampilkan daftar transaksi user.
     */
    public function index(Request $request)
    {
        $query = Transaction::with(['wallet', 'category'])
            ->whereHas('wallet', function ($q) {
                $q->where('user_id', Auth::id());
            });
    
        // Filter berdasarkan Dompet
        if ($request->filled('wallet_id')) {
            $query->where('wallet_id', $request->wallet_id);
        }
    
        // Filter berdasarkan Tipe (income/expense)
        if ($request->filled('type')) {
            $query->where('type', $request->type);
        }
    
        // Filter Rentang Tanggal
        if ($request->filled('start_date') && $request->filled('end_date')) {
            $query->whereBetween('date', [$request->start_date, $request->end_date]);
        }
    
        // Ubah ->paginate(15) menjadi ->get()
        $transactions = $query->orderBy('date', 'desc')->get();
    
        return response()->json([
            'status' => 'success',
            'data' => $transactions,
        ]);
    }

    /**
     * Simpan Transaksi Baru (Income / Expense)
     */
    public function store(Request $request)
{
    // Validasi Input
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
        // 1. Ambil dompet dengan Pessimistic Locking
        $wallet = Wallet::where('id', $request->wallet_id)
            ->where(function ($query) {
                $query->where('user_id', Auth::id())
                      ->orWhereHas('members', function ($q) {
                          $q->where('user_id', Auth::id());
                      });
            })
            ->lockForUpdate()
            ->first();

        if (!$wallet) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Dompet tidak ditemukan atau Anda tidak memiliki akses.',
            ], 403);
        }

        // 2. Cek apakah saldo mencukupi untuk Pengeluaran
        if ($request->type === 'expense' && $wallet->balance < $request->amount) {
            throw ValidationException::withMessages([
                'amount' => ['Saldo dompet tidak mencukupi untuk transaksi ini.']
            ]);
        }

        // 3. Cek Batas Anggaran (Kategori & Global Dompet)
        if ($request->type === 'expense') {
            $transactionDate = Carbon::parse($request->date);
            $startOfMonth    = $transactionDate->copy()->startOfMonth()->toDateString();
            $endOfMonth      = $transactionDate->copy()->endOfMonth()->toDateString();

            // A. Pengecekan Budget Spesifik Kategori
            $category = Categories::find($request->category_id);
            if ($category && $category->limit_amount > 0) {
                $currentCategoryExpense = Transaction::where('wallet_id', $wallet->id)
                    ->where('category_id', $category->id)
                    ->where('type', 'expense')
                    ->whereBetween('date', [$startOfMonth, $endOfMonth])
                    ->sum('amount');

                if (($currentCategoryExpense + $request->amount) > $category->limit_amount) {
                    throw ValidationException::withMessages([
                        'amount' => ["Transaksi ini melebihi batas anggaran kategori {$category->categories}."]
                    ]);
                }
            }

            // B. Pengecekan Budget Global Dompet (Tanpa pandang kategori)
            if ($wallet->limit_amount > 0) {
                $totalWalletExpense = Transaction::where('wallet_id', $wallet->id)
                    ->where('type', 'expense')
                    ->whereBetween('date', [$startOfMonth, $endOfMonth])
                    ->sum('amount');

                if (($totalWalletExpense + $request->amount) > $wallet->limit_amount) {
                    throw ValidationException::withMessages([
                        'amount' => ['Transaksi ini melebihi batas total anggaran bulanan dompet ini.']
                    ]);
                }
            }
        }

        // 4. Update Saldo Dompet
        if ($request->type === 'expense') {
            $wallet->balance -= $request->amount;
        } else {
            $wallet->balance += $request->amount;
        }
        $wallet->save();

        // 5. Catat Transaksi
        $transaction = Transaction::create([
            'user_id'           => Auth::id(),
            'wallet_id'         => $wallet->id,
            'group_id'          => $request->group_id ?? $wallet->group_id ?? null,
            'category_id'       => $request->category_id,
            'amount'            => $request->amount,
            'type'              => $request->type,
            'date'              => $request->date,
            'description'       => $request->description ?? $request->note,
            'recipient_account' => $request->recipient_account,
            'recipient_name'    => $request->recipient_name,
        ]);

        // 6. Hitung Rangkuman Budget Kategori & Dompet Terbaru
        $category = Categories::find($request->category_id);
        $transactionDate = Carbon::parse($request->date);
        $startOfMonth = $transactionDate->copy()->startOfMonth()->toDateString();
        $endOfMonth   = $transactionDate->copy()->endOfMonth()->toDateString();

        // Total pengeluaran spesifik kategori bulan ini
        $totalCategorySpent = Transaction::where('wallet_id', $wallet->id)
            ->where('category_id', $category->id)
            ->where('type', 'expense')
            ->whereBetween('date', [$startOfMonth, $endOfMonth])
            ->sum('amount');

        // Total pengeluaran global seluruh dompet bulan ini
        $totalWalletSpent = Transaction::where('wallet_id', $wallet->id)
            ->where('type', 'expense')
            ->whereBetween('date', [$startOfMonth, $endOfMonth])
            ->sum('amount');

        $remainingCategoryBudget = $category->limit_amount > 0 
            ? ($category->limit_amount - $totalCategorySpent) 
            : null;

        // 7. Response JSON Lengkap (Sinkron dengan Android)
        return response()->json([
            'status'  => 'success',
            'message' => 'Transaksi berhasil ditambahkan.',
            'data'    => $transaction->load(['wallet', 'category', 'group']),
            'wallet_summary' => [
                'wallet_id'          => $wallet->id,
                'new_balance'        => (float) $wallet->balance,
                'wallet_limit'       => (float) ($wallet->limit_amount ?? 0),
                'total_wallet_spent' => (float) $totalWalletSpent,
            ],
            'budget_summary' => [
                'category_id'      => $category->id,
                'category_name'    => $category->categories,
                'limit_amount'     => (float) $category->limit_amount,
                'total_spent'      => (float) $totalCategorySpent,
                'remaining_budget' => $remainingCategoryBudget != null ? (float) $remainingCategoryBudget : null,
            ]
        ], 201);
    });
}
    /**
     * Transfer Saldo antar Dompet
     */
    public function transfer(Request $request)
    {
        $request->validate([
            'from_wallet_id' => 'required|exists:wallets,id',
            'to_no_wallet'   => 'required|string|exists:wallets,no_wallet',
            'amount'         => 'required|numeric|min:0.01',
            'date'           => 'required|date',
            'description'    => 'nullable|string|max:255',
        ]);
    
        return DB::transaction(function () use ($request) {
            $userId = Auth::id();
    
            // 1. Ambil ID dompet penerima berdasarkan no_wallet terlebih dahulu
            $toWalletId = Wallet::where('no_wallet', $request->to_no_wallet)->value('id');
    
            if (!$toWalletId) {
                return response()->json([
                    'status'  => 'error',
                    'message' => 'Dompet tujuan tidak ditemukan.',
                ], 404);
            }
    
            // 2. Validasi pencegahan transfer ke dompet yang sama
            if ((int)$request->from_wallet_id === (int)$toWalletId) {
                throw ValidationException::withMessages([
                    'to_no_wallet' => ['Tidak dapat melakukan transfer ke dompet yang sama.']
                ]);
            }
    
            // 3. Urutkan ID untuk mencegah deadlock saat lockForUpdate
            $firstId  = min($request->from_wallet_id, $toWalletId);
            $secondId = max($request->from_wallet_id, $toWalletId);
    
            $wallets = Wallet::whereIn('id', [$firstId, $secondId])
                ->lockForUpdate()
                ->get()
                ->keyBy('id');
    
            $fromWallet = $wallets->get($request->from_wallet_id);
            $toWallet   = $wallets->get($toWalletId);
    
            if (!$fromWallet || !$toWallet) {
                return response()->json([
                    'status'  => 'error',
                    'message' => 'Satu atau kedua dompet tidak ditemukan.',
                ], 404);
            }
    
            // =========================================================================
            // VALIDASI HAK AKSES PENGIRIMAN (OUTBOUND CHECK)
            // =========================================================================
            if ($fromWallet->group_id) {
                // Cek apakah user adalah admin di grup dompet pengirim
                $isAdmin = DB::table('group_members')
                    ->where('group_id', $fromWallet->group_id)
                    ->where('user_id', $userId)
                    ->where('role', 'admin')
                    ->exists();
    
                if (!$isAdmin) {
                    return response()->json([
                        'status'  => 'error',
                        'message' => 'Hanya Admin Grup yang diizinkan melakukan transfer keluar dari dompet ini.',
                    ], 403);
                }
            } else {
                // Cek kepemilikan jika dompet pengirim adalah dompet pribadi
                if ($fromWallet->user_id !== $userId) {
                    return response()->json([
                        'status'  => 'error',
                        'message' => 'Anda tidak memiliki hak akses ke dompet asal ini.',
                    ], 403);
                }
            }
    
            // =========================================================================
            // EKSEKUSI TRANSFER SALDO
            // =========================================================================
            if ($fromWallet->balance < $request->amount) {
                throw ValidationException::withMessages([
                    'amount' => ['Saldo dompet asal tidak mencukupi untuk transfer.']
                ]);
            }
    
            // Pindah saldo
            $fromWallet->balance -= $request->amount;
            $fromWallet->save();
    
            $toWallet->balance += $request->amount;
            $toWallet->save();
    
            // Cari ID Kategori Transfer (Safe Fallback)
            $transferCategory = Categories::where('categories', 'Transfer')->first();
            $categoryId       = $transferCategory ? $transferCategory->id : null;
    
            $desc = $request->description ? " - {$request->description}" : '';
    
            // Catat pengeluaran pada dompet asal
            $outTransaction = Transaction::create([
                'user_id'     => $userId,
                'wallet_id'   => $fromWallet->id,
                'category_id' => $categoryId,
                'amount'      => $request->amount,
                'type'        => 'expense',
                'date'        => $request->date,
                'description' => "Transfer ke {$toWallet->name} ({$toWallet->no_wallet}){$desc}",
            ]);
    
            // Catat pemasukan pada dompet tujuan
            $inTransaction = Transaction::create([
                'user_id'     => $userId,
                'wallet_id'   => $toWallet->id,
                'category_id' => $categoryId,
                'amount'      => $request->amount,
                'type'        => 'income',
                'date'        => $request->date,
                'description' => "Transfer dari {$fromWallet->name} ({$fromWallet->no_wallet}){$desc}",
            ]);
    
            return response()->json([
                'status'  => 'success',
                'message' => 'Transfer saldo berhasil dilakukan.',
                'data'    => [
                    'out' => $outTransaction,
                    'in'  => $inTransaction,
                ],
            ]);
        });
    }
    /**
     * Top Up Saldo Dompet
     */
    public function getTopUpHistory(Request $request)
    {
        try {
            $user = Auth::user();
    
            // Cari ID Kategori Top Up jika ada
            $topUpCategory = Categories::where('name', 'Top Up')->first();
    
            // Ambil riwayat transaksi top up milik user
            $topUpTransactions = Transaction::with(['wallet', 'category'])
                ->where('user_id', $user->id)
                ->where(function ($query) use ($topUpCategory) {
                    if ($topUpCategory) {
                        $query->where('category_id', $topUpCategory->id)
                              ->orWhere('description', 'LIKE', '%Top Up%');
                    } else {
                        $query->where('description', 'LIKE', '%Top Up%');
                    }
                })
                ->orderBy('created_at', 'desc')
                ->paginate($request->input('per_page', 10));
    
            return response()->json([
                'status'  => 'success',
                'message' => 'Berhasil mengambil riwayat top up.',
                'data'    => $topUpTransactions
            ], 200);
    
        } catch (\Exception $e) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Gagal mengambil riwayat top up: ' . $e->getMessage()
            ], 500);
        }
    }
    
    /**
     * Update Transaksi (Memperbaiki logika kalkulasi & pergantian tipe)
     */
    public function update(Request $request, $id)
{
    $user = Auth::user();
    $userGroupIds = $user->groups()->pluck('groups.id');

    // 1. Otorisasi Transaksi
    $transaction = Transaction::where('id', $id)
        ->whereHas('wallet', function ($q) use ($user, $userGroupIds) {
            $q->where(function ($sub) use ($user, $userGroupIds) {
                $sub->where(function ($personal) use ($user) {
                    $personal->where('type', 'personal')
                             ->where('user_id', $user->id);
                })
                ->orWhere(function ($group) use ($userGroupIds) {
                    $group->where('type', 'group')
                          ->whereIn('group_id', $userGroupIds);
                });
            });
        })
        ->first();

    if (!$transaction) {
        return response()->json([
            'status'  => 'error',
            'message' => 'Transaksi tidak ditemukan atau Anda tidak memiliki akses.',
        ], 404);
    }

    // 2. Validasi Input
    $request->validate([
        'category_id' => [
            'required',
            'exists:categories,id',
            function ($attribute, $value, $fail) use ($transaction) {
                $categoryExists = Categories::where('id', $value)
                    ->where('wallet_id', $transaction->wallet_id)
                    ->exists();

                if (!$categoryExists) {
                    $fail('Kategori yang dipilih tidak sesuai dengan dompet ini.');
                }
            },
        ],
        'amount'            => 'required|numeric|min:0.01',
        'type'              => 'required|in:income,expense',
        'date'              => 'required|date',
        'description'       => 'nullable|string|max:255',
        'recipient_account' => 'nullable|string|max:50',
        'recipient_name'    => 'nullable|string|max:255',
    ]);

    // 3. Simpan Perubahan (Database Transaction)
    return DB::transaction(function () use ($request, $transaction) {
        $wallet = Wallet::where('id', $transaction->wallet_id)->lockForUpdate()->first();

        // A. Koreksi saldo dompet (Netralkan transaksi lama, lalu tambahkan/kurangi nominal baru)
        $tempBalance = $wallet->balance;
        if ($transaction->type === 'expense') {
            $tempBalance += $transaction->amount;
        } else {
            $tempBalance -= $transaction->amount;
        }

        $newBalance = ($request->type === 'expense')
            ? $tempBalance - $request->amount
            : $tempBalance + $request->amount;

        if ($newBalance < 0) {
            throw ValidationException::withMessages([
                'amount' => ['Perubahan transaksi ini membuat saldo dompet menjadi negatif.']
            ]);
        }

        // B. Cek Batas Budget Kategori
        if ($request->type === 'expense') {
            $category = Categories::find($request->category_id);

            if ($category && $category->limit_amount > 0) {
                $transactionDate = Carbon::parse($request->date);
                $startOfMonth    = $transactionDate->copy()->startOfMonth()->toDateString();
                $endOfMonth      = $transactionDate->copy()->endOfMonth()->toDateString();

                // Abaikan transaksi yang sedang di-update agar tidak dihitung dua kali
                $currentCategoryExpense = Transaction::where('wallet_id', $wallet->id)
                    ->where('category_id', $category->id)
                    ->where('type', 'expense')
                    ->where('id', '!=', $transaction->id)
                    ->whereBetween('date', [$startOfMonth, $endOfMonth])
                    ->sum('amount');

                if (($currentCategoryExpense + $request->amount) > $category->limit_amount) {
                    throw ValidationException::withMessages([
                        'amount' => ["Pengeluaran ini melebihi batas anggaran kategori {$category->categories}."]
                    ]);
                }
            }
        }

        // C. Update Database
        $wallet->balance = $newBalance;
        $wallet->save();

        $transaction->update([
            'category_id'       => $request->category_id,
            'amount'            => $request->amount,
            'type'              => $request->type,
            'date'              => $request->date,
            'description'       => $request->description,
            'recipient_account' => $request->recipient_account,
            'recipient_name'    => $request->recipient_name,
        ]);

        // D. Hitung Rangkuman Budget Terbaru untuk Kategori Terkait
        $category = Categories::find($request->category_id);
        $transactionDate = Carbon::parse($request->date);

        $totalSpent = Transaction::where('wallet_id', $wallet->id)
            ->where('category_id', $category->id)
            ->where('type', 'expense')
            ->whereBetween('date', [
                $transactionDate->copy()->startOfMonth()->toDateString(),
                $transactionDate->copy()->endOfMonth()->toDateString()
            ])
            ->sum('amount');

        $remainingBudget = $category->limit_amount > 0 
            ? ($category->limit_amount - $totalSpent) 
            : null;

        // E. Response JSON Lengkap (Struktur identik dengan store)
        return response()->json([
            'status'  => 'success',
            'message' => 'Catatan pengeluaran berhasil diperbarui.',
            'data'    => $transaction->load(['wallet', 'category', 'group']),
            'wallet_summary' => [
                'wallet_id'   => $wallet->id,
                'new_balance' => (float) $wallet->balance,
            ],
            'budget_summary' => [
                'category_id'      => $category->id,
                'category_name'    => $category->categories,
                'limit_amount'     => (float) $category->limit_amount,
                'total_spent'      => (float) $totalSpent,
                'remaining_budget' => $remainingBudget != null ? (float) $remainingBudget : null,
            ]
        ]);
    });
}

    /**
     * Hapus Transaksi & Kembalikan Saldo Dompet
     */
    public function destroy($id)
    {
        $transaction = Transaction::where('id', $id)
            ->whereHas('wallet', function ($q) {
                $q->where('user_id', Auth::id());
            })
            ->first();

        if (!$transaction) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Transaksi tidak ditemukan.',
            ], 404);
        }

        return DB::transaction(function () use ($transaction) {
            $wallet = Wallet::where('id', $transaction->wallet_id)
                ->lockForUpdate()
                ->first();

            // Revert Saldo Dompet
            if ($transaction->type === 'expense') {
                $wallet->balance += $transaction->amount;
            } else {
                // Cegah saldo minus jika pemasukan dihapus
                if ($wallet->balance < $transaction->amount) {
                    throw ValidationException::withMessages([
                        'message' => ['Membatalkan pemasukan ini akan membuat saldo dompet menjadi negatif.']
                    ]);
                }
                $wallet->balance -= $transaction->amount;
            }

            $wallet->save();
            $transaction->delete();

            return response()->json([
                'status'  => 'success',
                'message' => 'Transaksi berhasil dihapus dan saldo dompet dikembalikan.',
            ]);
        });
    }
}