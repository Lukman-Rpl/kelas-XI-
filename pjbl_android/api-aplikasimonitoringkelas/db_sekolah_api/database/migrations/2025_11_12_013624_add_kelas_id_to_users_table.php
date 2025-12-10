<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up()
    {
        Schema::table('users', function (Blueprint $table) {

            // Tambah kolom kelas_id setelah kolom role, hanya jika belum ada
            if (!Schema::hasColumn('users', 'kelas_id')) {
                $table->foreignId('kelas_id')
                    ->nullable()
                    ->after('role')
                    ->constrained('kelas')
                    ->nullOnDelete();
            }
        });
    }

    public function down()
    {
        Schema::table('users', function (Blueprint $table) {

            // Hapus foreign dan kolom jika ada
            if (Schema::hasColumn('users', 'kelas_id')) {
                $table->dropForeign(['kelas_id']);
                $table->dropColumn('kelas_id');
            }
        });
    }
};
