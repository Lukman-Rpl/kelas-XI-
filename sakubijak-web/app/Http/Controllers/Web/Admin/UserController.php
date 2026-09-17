<?php

namespace App\Http\Controllers\Web\Admin;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\User;

class UserController extends Controller
{
    /**
     * Hanya admin yang boleh akses
     */
    public function __construct()
    {
        $this->middleware(['auth','admin']);
    }

    /**
     * Daftar users
     */
    public function index(Request $request)
    {
        $allowedSort = ['name','email','created_at'];

        $sort = $request->get('sort','created_at');
        $direction = $request->get('direction','desc');

        if(!in_array($sort,$allowedSort)){
            $sort = 'created_at';
        }

        if(!in_array($direction,['asc','desc'])){
            $direction = 'desc';
        }

        $users = User::with(['wallet','groups'])
            ->orderBy($sort,$direction)
            ->paginate(10)
            ->withQueryString();

        return view('admin.users.index', compact(
            'users',
            'sort',
            'direction'
        ));
    }
}