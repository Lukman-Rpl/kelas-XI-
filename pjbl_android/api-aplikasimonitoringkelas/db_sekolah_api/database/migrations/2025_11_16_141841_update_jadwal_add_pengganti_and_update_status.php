<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('jadwal', function (Blueprint $table) {
            // Tambahkan kolom guru_pengganti_id
            $table->unsignedBigInteger('guru_pengganti_id')->nullable()->after('guru_id');

            // Buat foreign key
            $table->foreign('guru_pengganti_id')
                  ->references('id_guru')
                  ->on('guru')
                  ->nullOnDelete();

            // Ubah enum status
            $table->enum('status', ['hadir', 'tidak_hadir', 'terlambat'])
                  ->default('hadir')
                  ->change();
        });
    }

    public function down(): void
    {
        Schema::table('jadwal', function (Blueprint $table) {
            // Hapus foreign key & kolom
            $table->dropForeign(['guru_pengganti_id']);
            $table->dropColumn('guru_pengganti_id');

            // Kembalikan ke enum lama (sesuaikan jika sebelumnya berbeda)
            $table->enum('status', ['masuk', 'tidak_masuk'])
                  ->default('masuk')
                  ->change();
        });
    }
};
