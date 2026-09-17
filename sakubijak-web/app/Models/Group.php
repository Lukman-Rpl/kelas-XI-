<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\BelongsToMany;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\Relations\HasOne;

class Group extends Model
{
    use HasFactory;

    protected $fillable = [
        'name',
        'invite_code',
        'owner_id',
    ];

    /**
     * Owner / pembuat grup
     */
    public function owner(): BelongsTo
    {
        return $this->belongsTo(User::class, 'owner_id');
    }

    /**
     * Anggota grup (many to many via group_user)
     */
    public function users(): BelongsToMany
    {
        return $this->belongsToMany(User::class, 'group_user')
            ->withPivot(['role', 'joined_at']); // Sertakan joined_at jika ada
    }

    /**
     * Alias relasi untuk dipanggil sebagai $group->members
     */
    public function members(): BelongsToMany
    {
        return $this->users();
    }

    /**
     * Group Wallet milik grup ini (Opsi 1: type = 'group')
     * Menggunakan nama 'wallet' (1 Group = 1 Wallet)
     */
    public function wallet(): HasOne
    {
        return $this->hasOne(Wallet::class)->where('type', 'group');
    }

    /**
     * Transaksi yang terjadi di dalam grup
     */
    public function transactions(): HasMany
    {
        return $this->hasMany(Transaction::class);
    }

    /**
     * Undangan grup
     */
    public function invites(): HasMany
    {
        return $this->hasMany(GroupInvite::class);
    }
}