<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\GuruPengganti;

class GuruPenggantiSeeder extends Seeder
{
    public function run(): void
    {
        GuruPengganti::create([
            'guru_id' => 1,
            'guru_pengganti_id' => 3,
            'mapel_id' => 2,
            'kelas_id' => 1,
            'tanggal' => '2025-01-10',
            'jam' => '07:00 - 08:00',
            'keterangan' => 'Sakit',
        ]);
        
    }
}
