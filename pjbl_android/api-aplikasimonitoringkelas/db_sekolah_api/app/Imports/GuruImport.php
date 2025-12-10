<?php

namespace App\Imports;

use App\Models\Guru;
use Maatwebsite\Excel\Concerns\ToModel;
use Maatwebsite\Excel\Concerns\WithHeadingRow;

class GuruImport implements ToModel, WithHeadingRow
{
    public function model(array $row)
    {
        return new Guru([
            'nama_guru' => $row['nama_guru'],
            'nip'       => $row['nip'],
            'mapel_id'  => $row['mapel_id'],
        ]);
    }
}
