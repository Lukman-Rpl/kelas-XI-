<?php

namespace App\Http\Controllers\Web;

use App\Http\Controllers\Controller;
use App\Models\Transaction;
use App\Models\Wallet;
use Carbon\Carbon;
use Illuminate\Support\Facades\Auth;

class DashboardController extends Controller
{
    public function __construct()
    {
        $this->middleware('auth');
    }

    public function index()
    {
        $user = Auth::user();

        // 1. Ambil ID semua grup yang diikuti user (sebagai admin maupun anggota)
        $userGroupIds = $user->groups()->pluck('groups.id');

        // 2. Hitung Total Saldo Personal User (Menggunakan relasi wallet())
        $personalBalance = $user->wallet()->where('type', 'personal')->sum('balance');

        // 3. Hitung Total Saldo Dompet Grup yang diikuti User
        $groupBalance = Wallet::where('type', 'group')
            ->whereIn('group_id', $userGroupIds)
            ->sum('balance');

        // 4. Kueri Transaksi Terbaru (Personal + Grup)
        $recentTransactions = Transaction::with(['user', 'wallet', 'group', 'categories'])
            ->where(function ($query) use ($user, $userGroupIds) {
                $query->where('user_id', $user->id)
                      ->orWhereIn('group_id', $userGroupIds);
            })
            ->latest('date')
            ->take(5)
            ->get();

        // 5. Hitung Cashflow Bulan Ini (Personal + Grup tempat User bergabung)
        $startOfMonth = Carbon::now()->startOfMonth();
        $endOfMonth = Carbon::now()->endOfMonth();

        // Pemasukan bulan ini ( Personal + Grup )
        $monthlyIncome = Transaction::where(function ($query) use ($user, $userGroupIds) {
                $query->where('user_id', $user->id)
                      ->orWhereIn('group_id', $userGroupIds);
            })
            ->where('type', 'income')
            ->whereBetween('date', [$startOfMonth, $endOfMonth])
            ->sum('amount');

        // Pengeluaran bulan ini ( Personal + Grup )
        $monthlyExpense = Transaction::where(function ($query) use ($user, $userGroupIds) {
                $query->where('user_id', $user->id)
                      ->orWhereIn('group_id', $userGroupIds);
            })
            ->where('type', 'expense')
            ->whereBetween('date', [$startOfMonth, $endOfMonth])
            ->sum('amount');

        // 6. Ringkasan Dompet Personal & Grup
        $personalWalletsCount = $user->wallet()->where('type', 'personal')->count();
        $joinedGroupsCount = $userGroupIds->count();

        return view('dashboard.index', compact(
            'personalBalance',
            'groupBalance',
            'recentTransactions',
            'monthlyIncome',
            'monthlyExpense',
            'personalWalletsCount',
            'joinedGroupsCount'
        ));
    }
}