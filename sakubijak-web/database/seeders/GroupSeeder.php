<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\User;
use App\Models\Group;
use App\Models\Wallet;
use Illuminate\Support\Str;

class GroupSeeder extends Seeder
{
    public function run(): void
    {
        // Ambil user pertama sebagai owner utama
        $owner = User::first();

        if (! $owner) {
            $this->command->warn('Tidak ada user di database! Jalankan UserSeeder terlebih dahulu.');
            return;
        }

        // Daftar grup dummy yang ingin dibuat
        $groupsData = [
            [
                'name' => 'Tabungan Keluarga',
                'balance' => 5000000,
            ],
            [
                'name' => 'Kas Project SakuBijak',
                'balance' => 2500000,
            ],
            [
                'name' => 'Patungan Liburan',
                'balance' => 1500000,
            ],
        ];

        foreach ($groupsData as $data) {
            // 1. Buat Grup
            $group = Group::create([
                'name'        => $data['name'],
                'owner_id'    => $owner->id,
                'invite_code' => 'SBJ-' . strtoupper(Str::random(5)),
            ]);

            // 2. Buat Group Wallet (Opsi 1: type = 'group', user_id = null)
            Wallet::create([
                'name'     => $group->name,
                'type'     => 'group',
                'group_id' => $group->id,
                'user_id'  => null, // 👈 Nullable untuk wallet grup
                'balance'  => $data['balance'],
            ]);

            // 3. Attach Owner sebagai 'admin' di pivot table
            $group->users()->attach($owner->id, [
                'role' => 'admin',
            ]);

            // 4. Attach Member Lain (2-3 user secara acak) sebagai 'member'
            $otherUsers = User::where('id', '!=', $owner->id)->inRandomOrder()->take(rand(2, 3))->get();

            foreach ($otherUsers as $member) {
                // Gunakan syncWithoutDetaching agar tidak menduplikasi data pivot
                $group->users()->syncWithoutDetaching([
                    $member->id => ['role' => 'member']
                ]);
            }
        }
    }
}