<?php

use App\Http\Controllers\Api\AuthApiController;
use App\Http\Controllers\Api\DashboardController;
use App\Http\Controllers\Api\GroupWalletController;
use App\Http\Controllers\Api\MidtransWebhookController;
use App\Http\Controllers\Api\ProfileController;
use App\Http\Controllers\Api\ReportController;
use App\Http\Controllers\Api\TransactionController;
use App\Http\Controllers\Api\WalletController;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| Public API routes
|--------------------------------------------------------------------------
*/
Route::post('/auth/register', [AuthApiController::class, 'register'])
	->name('api.auth.register');
Route::post('/auth/login', [AuthApiController::class, 'login'])
	->name('api.auth.login');
Route::post('/auth/google', [AuthApiController::class, 'googleLogin'])
	->name('api.auth.google');

// Endpoint ini dipanggil langsung oleh Midtrans dan tidak memakai Sanctum.
Route::post('/midtrans/webhook', [MidtransWebhookController::class, 'handleNotification'])
	->name('api.midtrans.webhook');

Route::middleware('auth:sanctum')->group(function () {
	Route::post('/auth/logout', [AuthApiController::class, 'logout'])
		->name('api.auth.logout');
	Route::get('/auth/me', [AuthApiController::class, 'me'])
		->name('api.auth.me');

	Route::get('/dashboard', [DashboardController::class, 'getDashboardData'])
		->name('api.dashboard');

	Route::get('/profile', [ProfileController::class, 'show'])
		->name('api.profile.show');
	Route::post('/profile', [ProfileController::class, 'updateProfile'])
		->name('api.profile.update');
	Route::put('/profile/password', [ProfileController::class, 'changePassword'])
		->name('api.profile.password');

	Route::get('/wallets', [WalletController::class, 'index'])
		->name('api.wallets.index');
	Route::post('/wallets', [WalletController::class, 'store'])
		->name('api.wallets.store');
	Route::post('/wallets/join', [WalletController::class, 'joinGroup'])
		->name('api.wallets.join');
	Route::post('/wallets/{id}/activate', [WalletController::class, 'setActiveWallet'])
		->name('api.wallets.activate');
	Route::get('/wallets/{walletId}/invite-code', [WalletController::class, 'getInviteCode'])
		->name('api.wallets.invite-code');
	Route::get('/wallets/{id}/transactions', [WalletController::class, 'transactions'])
		->name('api.wallets.transactions');
	Route::post('/wallets/top-up', [WalletController::class, 'topUp'])
		->name('api.wallets.top-up');
	Route::get('/wallets/{id}', [WalletController::class, 'show'])
		->name('api.wallets.show');

	Route::get('/transactions', [TransactionController::class, 'index'])
		->name('api.transactions.index');
	Route::post('/transactions', [TransactionController::class, 'store'])
		->name('api.transactions.store');
	Route::post('/transactions/transfer', [TransactionController::class, 'transfer'])
		->name('api.transactions.transfer');
	Route::get('/transactions/top-up-history', [TransactionController::class, 'getTopUpHistory'])
		->name('api.transactions.top-up-history');
	Route::put('/transactions/{id}', [TransactionController::class, 'update'])
		->name('api.transactions.update');
	Route::delete('/transactions/{id}', [TransactionController::class, 'destroy'])
		->name('api.transactions.destroy');

	Route::post('/groups/join', [GroupWalletController::class, 'joinGroup'])
		->name('api.groups.join');
	Route::get('/groups/{groupId}/invite-code', [GroupWalletController::class, 'getInviteCode'])
		->name('api.groups.invite-code');

	Route::get('/reports', [ReportController::class, 'index'])
		->name('api.reports.index');
	Route::get('/reports/monthly', [ReportController::class, 'monthly'])
		->name('api.reports.monthly');
	Route::get('/reports/weekly', [ReportController::class, 'weekly'])
		->name('api.reports.weekly');
	Route::get('/reports/daily', [ReportController::class, 'daily'])
		->name('api.reports.daily');
});
