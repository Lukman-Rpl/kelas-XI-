<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Categories;
use App\Models\Transaction;
use App\Models\Wallet;
use Carbon\Carbon;
use Illuminate\Http\Request;

class DashboardController extends Controller
{
    public function getDashboardData(Request $request)
    {
        $user = $request->user();
        $walletId = $request->query('wallet_id');

        // 1. Dapatkan daftar ID wallet yang BISA DIAKSES oleh user
        $accessibleWalletIds = Wallet::where('user_id', $user->id)
            ->orWhereHas('group.users', function ($q) use ($user) {
                $q->where('users.id', $user->id);
            })
            ->pluck('id');

        if ($walletId) {
            if (!$accessibleWalletIds->contains($walletId)) {
                return response()->json([
                    'status'  => 'error',
                    'message' => 'Akses ke dompet ditolak.'
                ], 403);
            }
            $targetWalletIds = [$walletId];
        } else {
            $targetWalletIds = $accessibleWalletIds;
        }

        // 2. Format Label Bulan
        $currentMonthText = Carbon::now()->translatedFormat('F Y');

        // 3. Hitung Total Anggaran (budget_limit) & Pengeluaran Dompet
        $totalBudgetLimit = Wallet::whereIn('id', $targetWalletIds)->sum('budget_limit');

        $usedAmount = Transaction::whereIn('wallet_id', $targetWalletIds)
            ->where('type', 'expense')
            ->whereMonth('date', Carbon::now()->month)
            ->whereYear('date', Carbon::now()->year)
            ->sum('amount');

        $percentage = $totalBudgetLimit > 0 ? round(($usedAmount / $totalBudgetLimit) * 100) : 0;

        // 4. Status Indikator Kesehatan Dompet
        if ($percentage <= 60) {
            $status = "Aman";
        } elseif ($percentage <= 85) {
            $status = "Waspada";
        } else {
            $status = "Bahaya";
        }

        // 5. AMBIL ANGGARAN DARI TABEL CATEGORIES
        $categoryBudgets = Categories::whereIn('wallet_id', $targetWalletIds)
            ->where('limit_amount', '>', 0)
            ->get()
            ->map(function ($category) use ($targetWalletIds) {
                $spent = Transaction::whereIn('wallet_id', $targetWalletIds)
                    ->where('category_id', $category->id)
                    ->where('type', 'expense')
                    ->whereMonth('date', Carbon::now()->month)
                    ->whereYear('date', Carbon::now()->year)
                    ->sum('amount');

                return [
                    'id'            => $category->id,
                    'category_id'   => $category->id,
                    'category_name' => $category->categories ?? $category->name ?? 'Kategori',
                    'limit_amount'  => (int) $category->limit_amount,
                    'used_amount'   => (int) $spent,
                    'period'        => Carbon::now()->translatedFormat('F Y')
                ];
            });

        // 6. Ambil 5 Transaksi Terbaru
        $recentTransactions = Transaction::with('categories')
            ->whereIn('wallet_id', $targetWalletIds)
            ->orderBy('date', 'desc')
            ->orderBy('created_at', 'desc')
            ->take(5)
            ->get()
            ->map(function ($item) {
                return [
                    'id'       => $item->id,
                    'category' => $item->categories ? ($item->categories->categories ?? $item->categories->name) : 'Umum',
                    'amount'   => (int) $item->amount,
                    'type'     => $item->type,
                    'date'     => Carbon::parse($item->date)->format('d M Y')
                ];
            });

        // 7. Response JSON untuk Android
        return response()->json([
            'status' => 'success',
            'data'   => [
                // PERBAIKAN: Struktur disamakan persis dengan ProfileController
                'user_profile' => [
                    'id'            => $user->id,
                    'name'          => $user->name,
                    'email'         => $user->email,
                    'profile_photo' => $user->profile_photo,
                    'photo_url'     => $user->profile_photo ? asset('storage/' . $user->profile_photo) : null,
                ],
                'wallet_summary' => [
                    'category_name' => 'Total Anggaran Dompet',
                    'month_year'    => $currentMonthText,
                    'used_amount'   => (int) $usedAmount,
                    'total_amount'  => (int) $totalBudgetLimit,
                    'percentage'    => (int) $percentage,
                    'status_label'  => $status
                ],
                'category_budgets'    => $categoryBudgets,
                'recent_transactions' => $recentTransactions,
                'daily_tip'           => "Jangan lupa catat pengeluaran kecil seperti parkir dan jajan sore!"
            ]
        ], 200);
    }
}