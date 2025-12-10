<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::table('ijin', function (Blueprint $table) {
            // Menambahkan kolom hari setelah tanggal_selesai
            $table->string('hari')->nullable()->after('tanggal_selesai');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('ijin', function (Blueprint $table) {
            $table->dropColumn('hari');
        });
    }
};
