<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\User;
use App\Models\Wallet;
use App\Models\Transaction;
use App\Models\Group;
use App\Models\Categories; // Disesuaikan dengan Model Category (atau Categories jika nama model kamu jamak)
use Carbon\Carbon;

class TransactionSeeder extends Seeder
{
    public function run(): void
    {
        // 1. Ambil semua kategori dari database
        $categories = Categories::all();

        if ($categories->isEmpty()) {
            $this->command->warn('Tabel categories kosong! Silakan jalankan CategorySeeder terlebih dahulu.');
            return;
        }

        // ==========================================
        // A. TRANSAKSI UNTUK PERSONAL WALLETS
        // ==========================================
        $users = User::with(['wallet' => function ($query) {
            $query->where('type', 'personal');
        }])->get();

        foreach ($users as $user) {
            foreach ($user->wallet as $wallet) {
                $transactionCount = rand(5, 10);

                for ($i = 0; $i < $transactionCount; $i++) {
                    $type = fake()->randomElement(['expense', 'income']);
                    
                    // Filter kategori berdasarkan tipe transaksi
                    $category = $categories->where('type', $type)->shuffle()->first() 
                                ?? $categories->random();

                    Transaction::create([
                        'user_id'     => $user->id,
                        'wallet_id'   => $wallet->id,
                        'group_id'    => null, // 👈 WAJIB NULL untuk transaksi personal
                        'type'        => $type,
                        'amount'      => $type === 'expense' ? rand(10000, 200000) : rand(500000, 2000000),
                        'category_id' => $category->id, // Sesuaikan jika di migrasi kamu 'categories_id'
                        'description' => fake()->sentence(3),
                        'date'        => Carbon::now()->subDays(rand(0, 30)),
                    ]);
                }
            }
        }

        // ==========================================
// B. TRANSAKSI UNTUK GROUP WALLETS
// ==========================================
$groups = Group::with(['users', 'wallet'])->get();

foreach ($groups as $group) {
    // 1. Ambil objek wallet langsung (TANPA ->first())
    $groupWallet = $group->wallet;

    // 2. Cek apakah wallet ada dan grup memiliki anggota
    if ($groupWallet && $group->users->isNotEmpty()) {
        $groupTransactionCount = rand(8, 15);

        for ($i = 0; $i < $groupTransactionCount; $i++) {
            $randomMember = $group->users->random();
            $type = fake()->randomElement(['expense', 'income']);

            $category = $categories->where('type', $type)->shuffle()->first() 
                        ?? $categories->random();

            Transaction::create([
                'user_id'     => $randomMember->id,
                'wallet_id'   => $groupWallet->id, // Langsung ambil ->id dari objek $groupWallet
                'group_id'    => $group->id,
                'type'        => $type,
                'amount'      => $type === 'expense' ? rand(20000, 500000) : rand(1000000, 5000000),
                'category_id' => $category->id,
                'description' => fake()->sentence(3),
                'date'        => Carbon::now()->subDays(rand(0, 30)),
            ]);
        }
    }
}
    }
}