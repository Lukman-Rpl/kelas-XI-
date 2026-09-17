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
        Schema::create('wallets', function (Blueprint $table) {
            $table->id();
        
            // Nama Wallet (Contoh: "Tabungan", "Kas RT 05")
            $table->string('name');
        
            // Tipe: 'personal' atau 'group'
            $table->enum('type', ['personal', 'group'])->default('personal');
        
            // Saldo
            $table->decimal('balance', 15, 2)->default(0);
            $table->decimal('budget_limit', 15, 2)->nullable()->default(0);
            // Jika Personal Wallet -> user_id terisi, group_id NULL
            $table->foreignId('user_id')
                ->nullable()
                ->constrained()
                ->nullOnDelete();
        
            // Jika Group Wallet -> group_id terisi, user_id NULL
            $table->foreignId('group_id')
                ->nullable()
                ->constrained()
                ->cascadeOnDelete();
        
            $table->boolean('is_active')->default(true);
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('wallets');
    }
};
