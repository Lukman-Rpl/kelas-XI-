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
        Schema::create('group_user', function (Blueprint $table) {
            $table->id();

            $table->foreignId('user_id')
                ->constrained()
                ->cascadeOnDelete();

            $table->foreignId('group_id')
                ->constrained()
                ->cascadeOnDelete();

            $table->enum('role', ['admin', 'member'])
                ->default('member');

            $table->timestamp('joined_at')
                ->useCurrent();

            // ⭐ WAJIB karena withTimestamps()
            $table->timestamps();

            // mencegah user join grup yg sama dua kali
            $table->unique(['user_id', 'group_id']);
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('group_user');
    }
};
