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
        Schema::create('group_invites', function (Blueprint $table) {
            $table->id();
        
            $table->foreignId('group_id')
                  ->constrained()
                  ->cascadeOnDelete();
        
            $table->string('code')->unique();
            $table->timestamp('expired_at')->nullable();
            $table->integer('max_usage')->nullable();
            $table->integer('used_count')->default(0);
        
            $table->timestamps();
        });
        
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('group_invites');
    }
};
