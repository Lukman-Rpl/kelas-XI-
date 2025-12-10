<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class KelasSeeder extends Seeder
{
    public function run(): void
    {
        DB::table('kelas')->insert([
            [
                'nama_kelas' => 'X IPA 1',
                'jurusan_id' => 1,              // pastikan id ini ada
                'tahun_ajaran_id' => 1,         // pastikan id ini ada
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'nama_kelas' => 'XI IPS 2',
                'jurusan_id' => 2,
                'tahun_ajaran_id' => 2,
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'nama_kelas' => 'XII Bahasa 1',
                'jurusan_id' => 3,
                'tahun_ajaran_id' => 3,
                'created_at' => now(),
                'updated_at' => now(),
            ],
        ]);
    }
}
