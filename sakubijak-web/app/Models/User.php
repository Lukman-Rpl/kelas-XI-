<?php

namespace App\Models;

use App\Models\ActivityLog;
use App\Models\Group;
use App\Models\Transaction;
use App\Models\Wallet;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Relations\BelongsToMany;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Illuminate\Support\Str;
use Laravel\Sanctum\HasApiTokens;

class User extends Authenticatable
{
    use HasApiTokens, HasFactory, Notifiable;

    protected $fillable = [
        'name',
        'email',
        'google_id',
        'password',
        'profile_photo',
    ];

    protected $hidden = [
        'password',
        'remember_token',
    ];

    protected $casts = [
        'email_verified_at' => 'datetime',
        'password' => 'hashed',
    ];

    /*
    |--------------------------------------------------------------------------
    | RELATIONSHIPS
    |--------------------------------------------------------------------------
    */

    public function wallet(): HasMany
    {
        return $this->hasMany(Wallet::class);
    }

    public function member(): HasMany
    {
        return $this->hasMany(GroupUser::class);
    }

    public function groups(): BelongsToMany
    {
        return $this->belongsToMany(Group::class)
            ->withPivot(['role', 'joined_at'])
            ->withTimestamps();
    }

    public function initials(): string
    {
        return Str::of($this->name)
            ->explode(' ')
            ->map(fn (string $name) => Str::of($name)->substr(0, 1)->upper())
            ->take(2)
            ->implode('');
    }

    public function transactions(): HasMany
    {
        return $this->hasMany(Transaction::class);
    }

    public function activityLogs(): HasMany
    {
        return $this->hasMany(ActivityLog::class);
    }

    /*
    |--------------------------------------------------------------------------
    | AUTO CREATE WALLET
    |--------------------------------------------------------------------------
    */

    protected static function booted()
    {
        static::created(function ($user) {
            $user->wallet()->create([
                'name' => 'Utama',
                'balance' => 0
            ]);
        });
    }

    /*
    |--------------------------------------------------------------------------
    | PROFILE PHOTO URL
    |--------------------------------------------------------------------------
    */

    public function getProfilePhotoUrlAttribute()
    {
        if ($this->profile_photo) {
            return rtrim((string) config('filesystems.disks.public.url'), '/') . '/' . ltrim($this->profile_photo, '/');
        }

        return null;
    }
}