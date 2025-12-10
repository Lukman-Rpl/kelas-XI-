<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Ijin extends Model
{
    use HasFactory;

    protected $table = 'ijin'; // jika nama tabel bukan 'ijins'

    protected $fillable = [
        'guru_id',
        'nama_guru',
        'tanggal_mulai',
        'tanggal_selesai',
        'hari',
        'status',
        'keterangan',
    ];

    /**
     * Relasi ke tabel guru
     * guru_id → guru.id_guru
     */
    public function guru()
    {
        return $this->belongsTo(Guru::class, 'guru_id', 'id_guru');
    }
}
