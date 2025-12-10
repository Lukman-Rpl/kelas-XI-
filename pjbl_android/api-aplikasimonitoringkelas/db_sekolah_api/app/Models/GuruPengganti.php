<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class GuruPengganti extends Model
{
    protected $table = 'guru_pengganti';

    protected $fillable = [
        'guru_id',
        'guru_pengganti_id',
        'nama',
        'mapel_id',
        'kelas_id',
        'tanggal',
        'jam',
        'keterangan',
    ];
    

    public function guru()
    {
        return $this->belongsTo(Guru::class, 'guru_id', 'id_guru');
    }

    public function pengganti()
    {
        return $this->belongsTo(Guru::class, 'guru_pengganti_id', 'id_guru');
    }

    public function mapel()
    {
        return $this->belongsTo(Mapel::class, 'mapel_id');
    }

    public function kelas()
    {
        return $this->belongsTo(Kelas::class, 'kelas_id');
    }

    public static function boot()
{
    parent::boot();

    static::creating(function ($model) {
        if ($model->guru_pengganti_id) {
            $guru = \App\Models\Guru::find($model->guru_pengganti_id);
            if ($guru) {
                $model->nama = $guru->nama_guru;
            }
        }
    });

    static::updating(function ($model) {
        if ($model->guru_pengganti_id) {
            $guru = \App\Models\Guru::find($model->guru_pengganti_id);
            if ($guru) {
                $model->nama = $guru->nama_guru;
            }
        }
    });
}

}
