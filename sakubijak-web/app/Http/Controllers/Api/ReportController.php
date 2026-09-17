<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Wallet;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class ReportController extends Controller
{
    public function index(Request $request)
    {
        $period = $request->query('period', 'monthly');

        return match ($period) {
            'daily'   => $this->daily($request),
            'weekly'  => $this->weekly($request),
            'monthly' => $this->monthly($request),
            default   => $this->monthly($request),
        };
    }

    /**
     * Helper untuk mendapatkan daftar ID dompet yang dapat diakses oleh user
     */
    private function getAccessibleWalletIds($user, $requestedWalletId = null)
    {
        $accessibleWalletIds = Wallet::where('user_id', $user->id)
            ->orWhereHas('group.users', function ($q) use ($user) {
                $q->where('users.id', $user->id);
            })
            ->pluck('id');

        if ($requestedWalletId) {
            if (!$accessibleWalletIds->contains($requestedWalletId)) {
                return null; // Tidak punya akses
            }
            return [$requestedWalletId];
        }

        return $accessibleWalletIds;
    }

    /**
     * 1. Laporan Pengeluaran Bulanan (Monthly)
     * GET /api/reports/monthly?month=2026-07&wallet_id=1
     */
    public function monthly(Request $request)
    {
        $user = $request->user();
        $selectedMonth = $request->query('month', Carbon::now()->format('Y-m'));
        $walletIds = $this->getAccessibleWalletIds($user, $request->query('wallet_id'));

        if ($walletIds === null) {
            return response()->json(['status' => 'error', 'message' => 'Akses ke dompet ditolak.'], 403);
        }

        try {
            $date = Carbon::createFromFormat('Y-m', $selectedMonth);
        } catch (\Exception $e) {
            return response()->json(['status' => 'error', 'message' => 'Format bulan tidak valid. Gunakan YYYY-MM'], 400);
        }

        $year = $date->year;
        $month = $date->month;

        return $this->buildReportResponse(
            $walletIds,
            $date->translatedFormat('F Y'),
            function ($query) use ($year, $month) {
                $query->whereYear('transactions.date', $year)
                      ->whereMonth('transactions.date', $month);
            }
        );
    }

    /**
     * 2. Laporan Pengeluaran Mingguan (Weekly)
     * GET /api/reports/weekly?date=2026-07-29&wallet_id=1
     */
    public function weekly(Request $request)
    {
        $user = $request->user();
        $selectedDate = $request->query('date', Carbon::now()->format('Y-m-d'));
        $walletIds = $this->getAccessibleWalletIds($user, $request->query('wallet_id'));

        if ($walletIds === null) {
            return response()->json(['status' => 'error', 'message' => 'Akses ke dompet ditolak.'], 403);
        }

        try {
            $date = Carbon::parse($selectedDate);
        } catch (\Exception $e) {
            return response()->json(['status' => 'error', 'message' => 'Format tanggal tidak valid. Gunakan YYYY-MM-DD'], 400);
        }

        $startOfWeek = $date->copy()->startOfWeek(); // Senin
        $endOfWeek = $date->copy()->endOfWeek();     // Minggu

        $periodLabel = $startOfWeek->format('d M') . ' - ' . $endOfWeek->format('d M Y');

        return $this->buildReportResponse(
            $walletIds,
            $periodLabel,
            function ($query) use ($startOfWeek, $endOfWeek) {
                $query->whereBetween('transactions.date', [
                    $startOfWeek->format('Y-m-d'),
                    $endOfWeek->format('Y-m-d')
                ]);
            }
        );
    }

    /**
     * 3. Laporan Pengeluaran Harian (Daily)
     * GET /api/reports/daily?date=2026-07-29&wallet_id=1
     */
    public function daily(Request $request)
    {
        $user = $request->user();
        $selectedDate = $request->query('date', Carbon::now()->format('Y-m-d'));
        $walletIds = $this->getAccessibleWalletIds($user, $request->query('wallet_id'));

        if ($walletIds === null) {
            return response()->json(['status' => 'error', 'message' => 'Akses ke dompet ditolak.'], 403);
        }

        try {
            $date = Carbon::parse($selectedDate);
        } catch (\Exception $e) {
            return response()->json(['status' => 'error', 'message' => 'Format tanggal tidak valid. Gunakan YYYY-MM-DD'], 400);
        }

        return $this->buildReportResponse(
            $walletIds,
            $date->translatedFormat('d F Y'),
            function ($query) use ($date) {
                $query->whereDate('transactions.date', $date->format('Y-m-d'));
            }
        );
    }

    /**
     * Method Private untuk memproses akumulasi Pengeluaran, Pemasukan, dan Per-Kategori
     */
    private function buildReportResponse($walletIds, $periodLabel, callable $dateFilter)
    {
        // 1. Total Pengeluaran
        $expenseQuery = DB::table('transactions')
            ->whereIn('wallet_id', $walletIds)
            ->where('type', 'expense');
        $dateFilter($expenseQuery);
        $totalExpense = $expenseQuery->sum('amount');

        // 2. Total Pemasukan
        $incomeQuery = DB::table('transactions')
            ->whereIn('wallet_id', $walletIds)
            ->where('type', 'income');
        $dateFilter($incomeQuery);
        $totalIncome = $incomeQuery->sum('amount');

        // 3. Breakdown Pengeluaran per Kategori
        // Menggunakan kolom `categories.categories` sebagai pengganti `categories.name`
        $categoryQuery = DB::table('transactions')
            ->join('categories', 'transactions.category_id', '=', 'categories.id')
            ->select(
                'categories.id as category_id',
                'categories.categories as category_name',
                DB::raw(" '#2196F3' as color_hex "),
                DB::raw('SUM(transactions.amount) as total_amount')
            )
            ->whereIn('transactions.wallet_id', $walletIds)
            ->where('transactions.type', 'expense');
        
        $dateFilter($categoryQuery);

        $categoriesReport = $categoryQuery
            ->groupBy('categories.id', 'categories.categories')
            ->orderByDesc('total_amount')
            ->get();

        // 4. Hitung Persentase tiap Kategori
        $formattedCategories = $categoriesReport->map(function ($item) use ($totalExpense) {
            $percentage = $totalExpense > 0 ? round(($item->total_amount / $totalExpense) * 100) : 0;

            return [
                'category_id'      => $item->category_id,
                'name'             => $item->category_name,
                'color_hex'        => $item->color_hex,
                'amount'           => (int) $item->total_amount,
                'amount_formatted' => 'Rp ' . number_format($item->total_amount, 0, ',', '.'),
                'percentage'       => $percentage,
                'percentage_label' => $percentage . '%'
            ];
        });

        // 5. Kembalikan Response JSON yang Seragam untuk Android
        $netCashflow = (int) ($totalIncome - $totalExpense);
        $formattedNet = $netCashflow < 0 
            ? '-Rp ' . number_format(abs($netCashflow), 0, ',', '.')
            : 'Rp ' . number_format($netCashflow, 0, ',', '.');
        
        return response()->json([
            'status'  => 'success',
            'message' => 'Data laporan berhasil diambil',
            'data'    => [
                'period' => [
                    'label' => $periodLabel
                ],
                'summary' => [
                    'total_expense'           => (int) $totalExpense,
                    'total_expense_formatted' => 'Rp ' . number_format($totalExpense, 0, ',', '.'),
                    'total_income'            => (int) $totalIncome,
                    'total_income_formatted'  => 'Rp ' . number_format($totalIncome, 0, ',', '.'),
                    'net_cashflow'            => $netCashflow,
                    'net_cashflow_formatted'  => $formattedNet
                ],
                'categories' => $formattedCategories
            ]
        ], 200);
    }
}