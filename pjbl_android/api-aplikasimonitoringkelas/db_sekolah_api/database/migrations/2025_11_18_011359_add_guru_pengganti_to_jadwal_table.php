<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('jadwal', function (Blueprint $table) {

            if (!Schema::hasColumn('jadwal', 'guru_pengganti_id')) {
                $table->unsignedBigInteger('guru_pengganti_id')
                      ->nullable()
                      ->after('guru_id'); 

                $table->foreign('guru_pengganti_id')
                      ->references('id_guru')
                      ->on('guru')
                      ->onDelete('set null');
            }
        });
    }

    public function down(): void
    {
        Schema::table('jadwal', function (Blueprint $table) {
            if (Schema::hasColumn('jadwal', 'guru_pengganti_id')) {
                $table->dropForeign(['guru_pengganti_id']);
                $table->dropColumn('guru_pengganti_id');
            }
        });
    }
};
