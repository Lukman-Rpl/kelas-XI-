<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('jadwal', function (Blueprint $table) {
            // Ubah kolom status jadi enum
            $table->enum('status', ['masuk', 'tidak_masuk'])->default('masuk')->change();
        });
    }

    public function down(): void
    {
        Schema::table('jadwal', function (Blueprint $table) {
            // Kembalikan ke string biasa jika di-rollback
            $table->string('status', 50)->change();
        });
    }
};
