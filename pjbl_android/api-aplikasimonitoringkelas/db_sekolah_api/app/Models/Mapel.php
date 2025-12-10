<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Mapel extends Model
{
    use HasFactory;

    protected $table = 'mapel';
    protected $primaryKey = 'id'; // <- WAJIB karena tabel mapel memakai 'id'

    protected $fillable = ['nama_mapel'];

    // relasi ke guru
    public function guru()
    {
        return $this->hasMany(Guru::class, 'mapel_id', 'id');
    }
}
