<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\User;

class UserSeeder extends Seeder
{
    public function run()
    {
        // Buat 5 user, dompet otomatis dibuat oleh booted() di model User
        User::factory()->count(5)->create()->each(function ($user) {
            // Berikan saldo acak antara 100.000 - 1.000.000 ke dompet 'Utama'
            $user->wallet()->where('name', 'Utama')->update([
                'balance' => fake()->randomFloat(2, 100000, 1000000)
            ]);
        });
    }
}
