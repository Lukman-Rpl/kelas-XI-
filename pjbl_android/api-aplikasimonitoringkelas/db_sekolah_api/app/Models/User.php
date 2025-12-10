<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;

class User extends Authenticatable
{ 
    use HasFactory;

    protected $fillable = [
        'nama',
        'email',
        'password',
        'role',
        'kelas_id',     // ← WAJIB pakai kelas_id, bukan kelas
    ];

    protected $hidden = [
        'password',
    ];
    
    public function kelas() {
        return $this->belongsTo(Kelas::class, 'kelas_id');
    }
}
