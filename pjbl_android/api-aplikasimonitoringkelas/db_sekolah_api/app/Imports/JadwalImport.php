<?php

namespace App\Imports;

use App\Models\Jadwal;
use Maatwebsite\Excel\Concerns\ToModel;
use Maatwebsite\Excel\Concerns\WithHeadingRow;

class JadwalImport implements ToModel, WithHeadingRow
{
    public function model(array $row)
    {
        return new Jadwal([
            'hari'               => $row['hari'],
            'tanggal'            => $row['tanggal'],
            'kelas_id'           => $row['kelas_id'],
            'jam_ke'             => $row['jam_ke'],
            'sampai_jam'         => $row['sampai_jam'],
            'mapel'              => $row['mapel'],
            'guru_id'            => $row['guru_id'],
            'guru_pengganti_id'  => $row['guru_pengganti_id'] ?? null,
            'status'             => $row['status'] ?? 'hadir',
            'keterangan'         => $row['keterangan'] ?? null,
        ]);
    }
}
