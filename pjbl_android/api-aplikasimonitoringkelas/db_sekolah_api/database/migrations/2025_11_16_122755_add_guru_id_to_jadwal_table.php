<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('jadwal', function (Blueprint $table) {

            // tambah kolom guru_id jika belum ada
            if (!Schema::hasColumn('jadwal', 'guru_id')) {
                $table->unsignedBigInteger('guru_id')
                    ->nullable()
                    ->after('kelas_id');
            }

            // tambah foreign key
            $table->foreign('guru_id')
                ->references('id_guru')
                ->on('guru')
                ->nullOnDelete();
        });
    }

    public function down(): void
    {
        Schema::table('jadwal', function (Blueprint $table) {
            if (Schema::hasColumn('jadwal', 'guru_id')) {
                $table->dropForeign(['guru_id']);
                $table->dropColumn('guru_id');
            }
        });
    }
};
