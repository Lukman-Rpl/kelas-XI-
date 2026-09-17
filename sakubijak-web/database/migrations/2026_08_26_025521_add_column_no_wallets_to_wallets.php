<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;
use Illuminate\Support\Facades\DB;

return new class extends Migration {
    public function up(): void
    {
        // 1. Tambah kolom no_wallet secara NULLABLE terlebih dahulu
        if (!Schema::hasColumn('wallets', 'no_wallet')) {
            Schema::table('wallets', function (Blueprint $table) {
                $table->string('no_wallet', 20)->nullable()->after('id');
            });
        }

        // 2. Isi nilai no_wallet untuk semua data lama yang masih NULL atau kosong
        $wallets = DB::table('wallets')->whereNull('no_wallet')->orWhere('no_wallet', '')->get();

        foreach ($wallets as $wallet) {
            $generatedNo = '88' . str_pad($wallet->id, 8, '0', STR_PAD_LEFT);
            DB::table('wallets')->where('id', $wallet->id)->update([
                'no_wallet' => $generatedNo
            ]);
        }

        // 3. Ubah kolom menjadi NOT NULL dan UNIQUE
        Schema::table('wallets', function (Blueprint $table) {
            $table->string('no_wallet', 20)->nullable(false)->unique()->change();
        });
    }

    public function down(): void
    {
        Schema::table('wallets', function (Blueprint $table) {
            $table->dropColumn('no_wallet');
        });
    }
};