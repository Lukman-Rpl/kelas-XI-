<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('jadwal', function (Blueprint $table) {

            // Tambah kolom tanggal jika belum ada
            if (!Schema::hasColumn('jadwal', 'tanggal')) {
                $table->date('tanggal')->after('hari');
            }

            // Tambah kolom kelas_id jika belum ada
            if (!Schema::hasColumn('jadwal', 'kelas_id')) {
                $table->foreignId('kelas_id')
                    ->nullable()
                    ->after('tanggal')
                    ->constrained('kelas')
                    ->onDelete('cascade');
            }
        });
    }

    public function down(): void
    {
        Schema::table('jadwal', function (Blueprint $table) {

            // Hapus kelas_id jika ada
            if (Schema::hasColumn('jadwal', 'kelas_id')) {
                $table->dropForeign(['kelas_id']);
                $table->dropColumn('kelas_id');
            }

            // Hapus tanggal jika ada
            if (Schema::hasColumn('jadwal', 'tanggal')) {
                $table->dropColumn('tanggal');
            }
        });
    }
};
