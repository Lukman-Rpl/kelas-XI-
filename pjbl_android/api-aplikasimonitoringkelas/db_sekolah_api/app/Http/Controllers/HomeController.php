<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class HomeController extends Controller
{
    /**
     * Terapkan middleware auth agar hanya user login yang bisa mengakses.
     */
    public function __construct()
    {
        $this->middleware('auth'); // menggunakan Authenticate bawaan Laravel
    }

    /**
     * Tampilkan halaman utama setelah login.
     */
    public function index()
    {
        $user = Auth::user();

        // Jika admin → arahkan ke dashboard admin
        if ($user->role === 'admin') {
            return redirect()->route('admin.dashboard');
        }

        // Jika bukan admin → tampilkan halaman home biasa
        return view('home', compact('user'));
    }
}
