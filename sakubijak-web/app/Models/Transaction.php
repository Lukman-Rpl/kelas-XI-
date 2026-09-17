<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use App\Models\Group;
use App\Models\User;
use App\Models\Wallet;

class Transaction extends Model
{
    use HasFactory;

    /*
    |--------------------------------------------------------------------------
    | CONSTANTS
    |--------------------------------------------------------------------------
    */

    const TYPE_INCOME = 'income';
    const TYPE_EXPENSE = 'expense';

    /*
    |--------------------------------------------------------------------------
    | MASS ASSIGNMENT
    |--------------------------------------------------------------------------
    */

    protected $fillable = [
        'user_id',
        'order_id',
        'wallet_id',
        'group_id',
        'recipient_account',
        'recipient_name',
        'type',
        'amount',
        'category_id',
        'description',
        'date'
    ];

    /*
    |--------------------------------------------------------------------------
    | CASTING
    |--------------------------------------------------------------------------
    */

    protected $casts = [
        'amount' => 'decimal:2',
        'date' => 'date'
    ];

    protected static function booted()
    {
        static::creating(function ($transaction) {
            if (empty($transaction->date)) {
                $transaction->date = now();
            }
        });
    }

    /*
    |--------------------------------------------------------------------------
    | RELATIONSHIPS
    |--------------------------------------------------------------------------
    */

    public function user()
    {
        return $this->belongsTo(User::class);
    }

    public function wallet()
    {
        return $this->belongsTo(Wallet::class);
    }

    public function group()
    {
        return $this->belongsTo(Group::class);
    }

    public function categories(){
        return $this->belongsTo(Categories::class,'categories_id');
    }

    public function category()
    {
        return $this->belongsTo(Categories::class, 'category_id');
    }
    /*
    |--------------------------------------------------------------------------
    | SCOPES
    |--------------------------------------------------------------------------
    */

    public function scopeIncome($query)
    {
        return $query->where('type', self::TYPE_INCOME);
    }

    public function scopeExpense($query)
    {
        return $query->where('type', self::TYPE_EXPENSE);
    }
}