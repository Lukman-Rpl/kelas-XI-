<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('ijin', function (Blueprint $table) {
            $table->id();

            // relasi ke tabel guru
            $table->foreignId('guru_id')
                  ->constrained('guru', 'id_guru')
                  ->onDelete('cascade');

            // nama guru disimpan tanpa menggunakan after (karena create table)
            $table->string('nama_guru');

            // dua tanggal izin
            $table->date('tanggal_mulai');
            $table->date('tanggal_selesai');

            // keterangan
            $table->text('keterangan')->nullable();

            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('ijin');
    }
};
