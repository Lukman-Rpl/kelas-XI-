<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Guru extends Model
{
    use HasFactory;

    protected $table = 'guru';

    protected $primaryKey = 'id_guru';

    protected $fillable = [
        'nama_guru',
        'mapel_id',
        'nip',
    ];

    /**
     * Relasi ke tabel mapel
     * mapel_id → mapel.id
     */
    public function mapel()
    {
        return $this->belongsTo(Mapel::class, 'mapel_id', 'id');
    }

    /**
     * Relasi ke tabel jadwal
     * guru_id → guru.id_guru
     */
    public function jadwal()
    {
        return $this->hasMany(Jadwal::class, 'guru_id', 'id_guru');
    }
}
