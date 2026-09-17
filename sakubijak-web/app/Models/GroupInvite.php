<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Support\Str;

class GroupInvite extends Model
{
    use HasFactory;

    protected $fillable = [
        'group_id',
        'code',
        'expired_at',
        'max_usage',
        'used_count',
    ];

    // Menggunakan $casts sebagai pengganti $dates
    protected $casts = [
        'expired_at' => 'datetime',
        'max_usage'  => 'integer',
        'used_count' => 'integer',
    ];

    protected static function boot()
    {
        parent::boot();

        static::creating(function ($invite) {
            // Jika kolom 'code' kosong, generate otomatis kode unik
            if (empty($invite->code)) {
                $invite->code = self::generateUniqueCode();
            }
        });
    }

    // Fungsi pembantu untuk memastikan kode selalu Unik
    public static function generateUniqueCode()
    {
        do {
            $code = 'SB-' . strtoupper(Str::random(6));
        } while (self::where('code', $code)->exists());

        return $code;
    }

    // Method pembantu untuk mengecek apakah undangan masih bisa digunakan
    public function isValid(): bool
    {
        // 1. Cek kedaluwarsa
        if ($this->expired_at && $this->expired_at->isPast()) {
            return false;
        }

        // 2. Cek batas penggunaan
        if ($this->max_usage !== null && $this->used_count >= $this->max_usage) {
            return false;
        }

        return true;
    }

    public function group()
    {
        return $this->belongsTo(Group::class);
    }
}