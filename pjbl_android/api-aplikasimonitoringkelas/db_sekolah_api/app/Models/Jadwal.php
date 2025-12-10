<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Jadwal extends Model
{
    use HasFactory;

    protected $table = 'jadwal';

    protected $fillable = [
        'hari',
        'tanggal',
        'kelas_id',
        'jam_ke',
        'sampai_jam',
        'mapel_id',           // tambahkan mapel_id
        'mapel',              // nama mapel tetap disimpan
        'guru_id',
        'guru_pengganti_id',
        'status',
        'keterangan'
    ];

    // Relasi ke kelas
    public function kelas()
    {
        return $this->belongsTo(Kelas::class, 'kelas_id');
    }

    // Relasi ke guru utama
    public function guru()
    {
        return $this->belongsTo(Guru::class, 'guru_id');
    }

    // Relasi ke guru pengganti
    public function pengganti()
    {
        return $this->belongsTo(Guru::class, 'guru_pengganti_id');
    }

    // Relasi ke mapel
    public function mapel()
    {
        return $this->belongsTo(Mapel::class, 'mapel_id');
    }
}
