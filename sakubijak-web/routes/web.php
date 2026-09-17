<?php

use App\Http\Controllers\Web\DashboardController;
use App\Http\Controllers\Web\GroupController;
use App\Http\Controllers\Web\ProfileController;
use App\Http\Controllers\Web\TransactionController;
use App\Http\Controllers\Web\WalletController;
use Illuminate\Support\Facades\Route;

Route::redirect('/', '/dashboard');

Route::middleware(['auth'])->group(function () {
    Route::get('/dashboard', [DashboardController::class, 'index'])->name('dashboard');

    Route::get('/wallets', [WalletController::class, 'index'])->name('wallets.index');
    Route::post('/wallets', [WalletController::class, 'store'])->name('wallets.store');
    Route::get('/wallets/{id}', [WalletController::class, 'show'])->name('wallets.show');
    Route::get('/wallets/{id}/edit', [WalletController::class, 'edit'])->name('wallets.edit');
    Route::put('/wallets/{id}', [WalletController::class, 'update'])->name('wallets.update');
    Route::delete('/wallets/{id}', [WalletController::class, 'destroy'])->name('wallets.destroy');
    Route::post('/wallets/{walletId}/budget', [WalletController::class, 'setBudget'])->name('wallets.budget');
    Route::post('/wallets/{wallet}/categories', [WalletController::class, 'storeCategory'])->name('wallets.categories.store');
    Route::delete('/wallets/{wallet}/categories/{categoryId}', [WalletController::class, 'destroyCategory'])->name('wallets.categories.destroy');

    Route::get('/transactions', [TransactionController::class, 'index'])->name('transactions.index');
    Route::post('/transactions', [TransactionController::class, 'store'])->name('transactions.store');
    Route::get('/transactions/{id}', [TransactionController::class, 'show'])->name('transactions.show');

    Route::get('/groups', [GroupController::class, 'index'])->name('groups.index');
    Route::get('/groups/create', [GroupController::class, 'create'])->name('groups.create');
    Route::post('/groups', [GroupController::class, 'store'])->name('groups.store');
    Route::post('/groups/join', [GroupController::class, 'join'])->name('groups.join');
    Route::get('/groups/{id}', [GroupController::class, 'show'])->name('groups.show');
    Route::get('/groups/{id}/members', [GroupController::class, 'members'])->name('groups.members');
    Route::delete('/groups/{groupId}/members/{userId}', [GroupController::class, 'kickMember'])->name('groups.members.kick');
    Route::delete('/groups/{id}', [GroupController::class, 'destroy'])->name('groups.destroy');

    Route::get('/profile', [ProfileController::class, 'index'])->name('profile.index');
    Route::get('/profile/edit', [ProfileController::class, 'edit'])->name('profile.edit');
    Route::put('/profile', [ProfileController::class, 'update'])->name('profile.update');
    Route::put('/profile/password', [ProfileController::class, 'updatePassword'])->name('profile.password');
});

require __DIR__.'/auth.php';
