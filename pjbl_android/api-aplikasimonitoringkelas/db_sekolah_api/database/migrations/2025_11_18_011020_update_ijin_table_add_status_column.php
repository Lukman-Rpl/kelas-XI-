<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('ijin', function (Blueprint $table) {

            // Tambahkan kolom status setelah tanggal_selesai
            if (!Schema::hasColumn('ijin', 'status')) {
                $table->enum('status', ['sakit', 'ijin'])
                      ->default('ijin')
                      ->after('tanggal_selesai');
            }
        });
    }

    public function down(): void
    {
        Schema::table('ijin', function (Blueprint $table) {
            if (Schema::hasColumn('ijin', 'status')) {
                $table->dropColumn('status');
            }
        });
    }
};
