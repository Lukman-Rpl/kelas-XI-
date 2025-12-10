<?php

namespace App\Imports;

use App\Models\GuruPengganti;
use Maatwebsite\Excel\Concerns\ToModel;
use Maatwebsite\Excel\Concerns\WithHeadingRow;

class GuruPenggantiImport implements ToModel, WithHeadingRow
{
    public function model(array $row)
    {
        return new GuruPengganti([
            'guru_id'            => $row['guru_id'],
            'guru_pengganti_id'  => $row['guru_pengganti_id'],
            'nama'               => $row['nama'],
            'mapel_id'           => $row['mapel_id'],
            'kelas_id'           => $row['kelas_id'],
            'tanggal'            => $row['tanggal'],
            'jam'                => $row['jam'],
            'keterangan'         => $row['keterangan'],
        ]);
    }
}
