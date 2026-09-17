<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class GroupUser extends Model
{
  protected $table='group_user';
  protected $fillable=[
    'user_id',
    'group_id',
    'role',
    'joined_at'
  ];

  public function group(){
    return $this->belongsTo(Group::class,'group_id');
  }

  public function member(){
    return $this->belongsTo(User::class,'user_id');
  }
}
