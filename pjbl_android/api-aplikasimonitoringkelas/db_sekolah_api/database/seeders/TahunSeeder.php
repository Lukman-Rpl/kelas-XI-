<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class TahunSeeder extends Seeder
{
    public function run()
    {
        DB::table('tahun_ajaran')->insert([
            [
                'tahun' => '2022/2023',
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'tahun' => '2023/2024',
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'tahun' => '2024/2025',
                'created_at' => now(),
                'updated_at' => now(),
            ]
        ]);
    }
}
