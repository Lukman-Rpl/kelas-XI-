<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;
use Carbon\Carbon;

class CategoriesSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $now = Carbon::now();

        $categories = [
            ['categories' => 'Makanan & Minuman', 'created_at' => $now, 'updated_at' => $now],
            ['categories' => 'Transportasi',     'created_at' => $now, 'updated_at' => $now],
            ['categories' => 'Belanja',          'created_at' => $now, 'updated_at' => $now],
            ['categories' => 'Tagihan & Utilitas','created_at' => $now, 'updated_at' => $now],
            ['categories' => 'Hiburan',          'created_at' => $now, 'updated_at' => $now],
            ['categories' => 'Pendidikan',        'created_at' => $now, 'updated_at' => $now],
            ['categories' => 'Lainnya',           'created_at' => $now, 'updated_at' => $now],
            ['categories' => 'Gaji',              'created_at' => $now, 'updated_at' => $now],
            ['categories' => 'Bonus & Komisi',   'created_at' => $now, 'updated_at' => $now],
            ['categories' => 'Investasi',        'created_at' => $now, 'updated_at' => $now],
        ];

        DB::table('categories')->insert($categories);
    }
}