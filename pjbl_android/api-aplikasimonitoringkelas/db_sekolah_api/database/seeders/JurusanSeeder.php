<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class JurusanSeeder extends Seeder
{
    public function run(): void
    {
        DB::table('jurusan')->insert([
            [
                'nama_jurusan' => 'IPA',
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'nama_jurusan' => 'IPS',
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'nama_jurusan' => 'Bahasa',
                'created_at' => now(),
                'updated_at' => now(),
            ],
        ]);
    }
}
