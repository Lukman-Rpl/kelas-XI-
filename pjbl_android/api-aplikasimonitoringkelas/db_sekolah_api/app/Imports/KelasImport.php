<?php

namespace App\Imports;

use App\Models\Kelas;
use Maatwebsite\Excel\Concerns\ToModel;
use Maatwebsite\Excel\Concerns\WithHeadingRow;

class KelasImport implements ToModel, WithHeadingRow
{
    public function model(array $row)
    {
        return new Kelas([
            'nama_kelas'       => $row['nama_kelas'],
            'jurusan_id'       => $row['jurusan_id'],        // sesuai tabel
            'tahun_ajaran_id'  => $row['tahun_ajaran_id'],   // sesuai tabel
        ]);
    }
}
