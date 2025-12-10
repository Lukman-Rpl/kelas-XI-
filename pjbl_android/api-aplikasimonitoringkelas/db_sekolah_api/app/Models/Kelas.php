<?php
namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Kelas extends Model
{
    protected $table = 'kelas'; 
    protected $fillable = ['nama_kelas','jurusan_id','tahun_ajaran_id'];

    public function jurusan() {
        return $this->belongsTo(Jurusan::class);
    }

    public function tahunAjaran() {
        return $this->belongsTo(TahunAjaran::class);
    }

    public function users() {
        return $this->hasMany(User::class);
    }

    public function jadwal() {
        return $this->hasMany(Jadwal::class);
    }
}

?>