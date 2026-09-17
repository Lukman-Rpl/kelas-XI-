<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\Relations\HasManyThrough;

class Wallet extends Model
{
    use HasFactory;

    protected $fillable = [
        'no_wallet',
        'name',
        'type',
        'balance',
        'budget_limit',
        'reset_type',
        'user_id',
        'group_id',
        'is_active',
    ];

    protected $casts = [
        'balance'      => 'double',
        'budget_limit' => 'double',
        'is_active'    => 'boolean',
        'user_id'      => 'integer',
        'group_id'     => 'integer',
    ];

    protected $attributes = [
        'balance'   => 0,
        'type'      => 'personal',
        'is_active' => true,
    ];

    protected static function booted(){
      static::creating(function ($wallet){
        if(empty($wallet->no_wallet)){
            $wallet->no_wallet = self::generateUniqueWalletNumber();
        }
      });    
    }

    private static function generateUniqueWalletNumber(){
        do{
            $number = '88' . mt_rand(10000008, 99999999);
        }while (self::where('no_wallet', $number)->exists());

        return $number;
    }

    /**
     * Relasi ke Pemilik (User)
     */
    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }

    /**
     * Relasi ke Group
     */
    public function group(): BelongsTo
    {
        return $this->belongsTo(Group::class);
    }

    public function categories(){
        return $this->hasMany(Categories::class);
    }

    /**
     * Relasi ke Anggota Grup (User) melalui tabel group_user (GroupUser model)
     */
    public function members(): HasManyThrough
    {
        return $this->hasManyThrough(
            User::class,
            GroupUser::class, // Menggunakan model GroupUser
            'group_id',       // Foreign key pada tabel group_user
            'id',             // Foreign key pada tabel users
            'group_id',       // Local key pada tabel wallets
            'user_id'         // Local key pada tabel group_user
        );
    }

    /**
     * Relasi ke Transactions
     */
    public function transactions(): HasMany
    {
        return $this->hasMany(Transaction::class);
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    public function isPersonal(): bool
    {
        return strtolower($this->type) === 'personal';
    }

    public function isGroup(): bool
    {
        return strtolower($this->type) === 'group';
    }
}