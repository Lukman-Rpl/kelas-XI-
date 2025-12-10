<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;

class UserSeeder extends Seeder
{
    public function run(): void
    {
        DB::table('users')->insert([
            ['nama'      => 'Administrator',
            'email'     => 'admin@sekolah.com',
            'password'  => Hash::make('password'), // ganti dengan password yang kamu mau
            'role'      => 'admin',                // enum: admin
            'kelas_id'  => null,                   // admin tidak punya kelas
            'created_at'=> now(),
            'updated_at'=> now(),
            ]
        ]);
    }
}
