<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use App\Models\Transaction;

class Categories extends Model
{
    protected $table= 'categories';

    protected $fillable=[
        'wallet_id',
        'categories',
        'limit_amount',
        'user_id'
    ];

    public function transaction(){
        return $this->hasMany(Transaction::class);
    }

    public function wallet(){
        return $this->belongsTo('wallet_id',Wallet::class);
    }
}
