<?php

namespace App\Imports;

use App\Models\User;
use Illuminate\Support\Facades\Hash;
use Maatwebsite\Excel\Concerns\ToModel;
use Maatwebsite\Excel\Concerns\WithHeadingRow;

class UserImport implements ToModel, WithHeadingRow
{
    public function model(array $row)
    {
        return new User([
            'nama'      => $row['nama'],
            'email'     => $row['email'],
            'password'  => Hash::make($row['password']),
            'role'      => $row['role'],
            'kelas_id'  => $row['kelas_id'] ?? null, // diperbaiki
        ]);
    }
}
