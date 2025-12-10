<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up()
    {
        Schema::create('kelas', function (Blueprint $table) {
            $table->id();
            $table->string('nama_kelas'); // Contoh: X, XI, XII
            $table->foreignId('jurusan_id')->constrained('jurusan')->cascadeOnDelete();
            $table->foreignId('tahun_ajaran_id')->constrained('tahun_ajaran')->cascadeOnDelete();
            $table->timestamps();
        
            $table->unique(['nama_kelas','jurusan_id','tahun_ajaran_id']); // agar tidak ada duplikat
        });
        
    }
    
    public function down()
    {
        Schema::dropIfExists('kelas');
    }
    
};
