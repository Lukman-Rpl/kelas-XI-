<?php

namespace App\Imports;

use App\Models\Ijin;
use Maatwebsite\Excel\Concerns\ToModel;
use Maatwebsite\Excel\Concerns\WithHeadingRow;

class IjinImport implements ToModel, WithHeadingRow
{
    public function model(array $row)
    {
        return new Ijin([
            'guru_id'         => $row['guru_id'],
            'nama_guru'       => $row['nama_guru'],
            'tanggal_mulai'   => $row['tanggal_mulai'],
            'tanggal_selesai' => $row['tanggal_selesai'],
            'status'          => $row['status'],        // sakit / ijin
            'keterangan'      => $row['keterangan'] ?? null,
        ]);
    }
}
