<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use App\Models\Guru;
use App\Models\User;
use App\Models\Mapel;
use App\Models\Jadwal;
use Maatwebsite\Excel\Facades\Excel;
use App\Imports\UserImport;
use Illuminate\Http\Request;

class DashboardController extends Controller
{
    public function index()
    {
        return view('admin.dashboard', [
            'totalGuru'   => Guru::count(),
            'totalUser'   => User::count(),
            'totalMapel'  => Mapel::count(),
            'totalJadwal' => Jadwal::count()
        ]);
    }

    public function importUsers(Request $request)
    {
        $request->validate([
            'file' => 'required|mimes:xlsx,xls'
        ]);

        Excel::import(new UserImport(), $request->file('file'));

        return back()->with('success', 'Data user berhasil diimport!');
    }
}
