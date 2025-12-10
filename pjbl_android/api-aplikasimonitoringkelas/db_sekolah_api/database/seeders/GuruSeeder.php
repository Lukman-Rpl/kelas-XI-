<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class GuruSeeder extends Seeder
{
    public function run(): void
    {
        DB::table('guru')->insert([
            [
                'nama_guru' => 'Siti Nurhaliza',
                'mapel_id' => 1, // Matematika
                'nip' => '198012312020022001',
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'nama_guru' => 'Budi Santoso',
                'mapel_id' => 2, // Bahasa Indonesia
                'nip' => '198503012015031002',
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'nama_guru' => 'Rina Marlina',
                'mapel_id' => 3, // Bahasa Inggris
                'nip' => '198711112010122003',
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'nama_guru' => 'Joko Pratama',
                'mapel_id' => 4, // IPA
                'nip' => '197912152005041004',
                'created_at' => now(),
                'updated_at' => now(),
            ],
            [
                'nama_guru' => 'Dewi Kartika',
                'mapel_id' => 5, // IPS
                'nip' => '198606062009042005',
                'created_at' => now(),
                'updated_at' => now(),
            ],
        ]);
    }
}
