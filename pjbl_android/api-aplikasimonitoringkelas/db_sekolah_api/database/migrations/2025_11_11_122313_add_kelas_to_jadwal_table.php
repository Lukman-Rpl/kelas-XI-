<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    public function up(): void {
        Schema::table('jadwal', function (Blueprint $table) {
            // hapus kolom kelas lama jika masih ada
            if (Schema::hasColumn('jadwal', 'kelas')) {
                $table->dropColumn('kelas');
            }
        });
    }

    public function down(): void {
        Schema::table('jadwal', function (Blueprint $table) {
            $table->dropForeign(['kelas_id']);
            $table->dropColumn('kelas_id');
        });
    }
};