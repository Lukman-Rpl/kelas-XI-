<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('guru_pengganti', function (Blueprint $table) {
            $table->id();
            $table->unsignedBigInteger('guru_id'); // guru yang berhalangan
            $table->unsignedBigInteger('guru_pengganti_id'); // guru yang menggantikan
            $table->unsignedBigInteger('mapel_id')->nullable();
            $table->unsignedBigInteger('kelas_id')->nullable();
            $table->date('tanggal');
            $table->string('jam')->nullable();
            $table->string('keterangan')->nullable();

            $table->timestamps();

            // Relasi
            $table->foreign('guru_id')->references('id_guru')->on('guru')->onDelete('cascade');
            $table->foreign('guru_pengganti_id')->references('id_guru')->on('guru')->onDelete('cascade');
            $table->foreign('mapel_id')->references('id')->on('mapel')->onDelete('set null');
            $table->foreign('kelas_id')->references('id')->on('kelas')->onDelete('set null');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('guru_pengganti');
    }
};
