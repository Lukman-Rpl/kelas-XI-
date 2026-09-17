<?php

namespace Database\Seeders;

use App\Models\Group;
use App\Models\User;
use App\Models\Wallet;
use Illuminate\Database\Seeder;

class WalletSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // 1. Ambil sampel user dan group jika ada
        $user = User::first();
        $group = Group::first();

        // 2. Seeder untuk Personal Wallet (Jika user ada)
        if ($user) {
            Wallet::create([
                'name' => 'Dompet Utama',
                'type' => 'personal',
                'balance' => 5000000,
                'budget_limit' => 3000000,
                'user_id' => $user->id,
                'group_id' => null,
                'is_active' => true,
            ]);

            Wallet::create([
                'name' => 'Tabungan',
                'type' => 'personal',
                'balance' => 10000000,
                'budget_limit' => 0,
                'user_id' => $user->id,
                'group_id' => null,
                'is_active' => true,
            ]);
        }

        // 3. Seeder untuk Group Wallet (Jika group ada)
        if ($group) {
            Wallet::create([
                'name' => 'Kas Group ' . $group->name,
                'type' => 'group',
                'balance' => 2500000,
                'budget_limit' => 5000000,
                'user_id' => null,
                'group_id' => $group->id,
                'is_active' => true,
            ]);
        }
    }
}