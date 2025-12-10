<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('guru', function (Blueprint $table) {

            // Hapus kolom mapel lama jika masih ada
            if (Schema::hasColumn('guru', 'mapel')) {
                $table->dropColumn('mapel');
            }

            // Tambahkan kolom mapel_id
            $table->foreignId('mapel_id')
                ->nullable()
                ->after('nama_guru')
                ->constrained('mapel')
                ->onDelete('set null');
        });
    }

    public function down(): void
    {
        Schema::table('guru', function (Blueprint $table) {
            $table->dropForeign(['mapel_id']);
            $table->dropColumn('mapel_id');
            $table->string('mapel')->nullable();
        });
    }
};
