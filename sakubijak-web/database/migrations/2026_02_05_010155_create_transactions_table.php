<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('transactions', function (Blueprint $table) {
            $table->id();

            // USER yang memicu/melakukan transaksi
            $table->foreignId('user_id')
                ->constrained()
                ->cascadeOnDelete();

            // WALLET wajib terisi (baik itu Personal Wallet maupun Group Wallet)
            $table->foreignId('wallet_id')
                ->constrained()
                ->cascadeOnDelete();

            // GROUP terisi HANYA jika transaksi dilakukan di Group Wallet (Personal = NULL)
            $table->foreignId('group_id')
                ->nullable()
                ->constrained()
                ->cascadeOnDelete();

            // KATEGORI (disarankan nama singular: category_id)
            $table->foreignId('category_id')
                ->constrained()
                ->restrictOnDelete(); // Mencegah transaksi hilang jika kategori dihapus

            $table->enum('type', ['income', 'expense']);
            $table->decimal('amount', 15, 2);

            $table->string('description')->nullable();
            $table->dateTime('date');

            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('transactions');
    }
};