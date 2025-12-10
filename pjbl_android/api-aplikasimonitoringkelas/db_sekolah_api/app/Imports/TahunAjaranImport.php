<?php

namespace App\Imports;

use App\Models\TahunAjaran;
use Maatwebsite\Excel\Concerns\ToModel;
use Maatwebsite\Excel\Concerns\WithHeadingRow;

class TahunAjaranImport implements ToModel, WithHeadingRow
{
    public function model(array $row)
    {
        return new TahunAjaran([
            'tahun' => $row['tahun'], // sesuai nama kolom di database
        ]);
    }
}
