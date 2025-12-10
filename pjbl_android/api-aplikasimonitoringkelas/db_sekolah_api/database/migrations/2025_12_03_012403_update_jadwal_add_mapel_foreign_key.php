<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('jadwal', function (Blueprint $table) {
            // Tambahkan kolom mapel_id baru
            $table->unsignedBigInteger('mapel_id')->after('sampai_jam')->nullable();

            // Tambahkan foreign key
            $table->foreign('mapel_id')
                  ->references('id')
                  ->on('mapel')
                  ->onDelete('cascade');
        });
    }

    public function down(): void
    {
        Schema::table('jadwal', function (Blueprint $table) {
            $table->dropForeign(['mapel_id']);
            $table->dropColumn('mapel_id');
        });
    }
};
