<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\User;
use App\Models\Kelas;
use Illuminate\Support\Facades\Hash;
use Maatwebsite\Excel\Facades\Excel;
use App\Imports\UserImport;

class UserController extends Controller
{
    public function index() 
{
    $users = User::with('kelas')->get(); 
    $kelas = Kelas::all(); 
    
    // dd($kelas); // TEST

    return view('admin.users.index', compact('users','kelas'));
}


    public function create() 
    { 
        $kelas = Kelas::all(); 
        return view('admin.users.create', compact('kelas')); 
    }

    public function store(Request $request) 
    {

        logger('STORE TERPANGGIL');
    logger($request->all());

        $request->validate([
            'nama'=>'required|string',
            'email'=>'required|email|unique:users',
            'password'=>'required|string|min:6',
            'role'=>'required|in:admin,kurikulum,siswa,kepala-sekolah',
            'kelas_id'=>'nullable|exists:kelas,id'
        ]);

        
    $result = User::create([
    'nama'     => $request->nama,
    'email'    => $request->email,
    'password' => Hash::make($request->password),
    'role'     => $request->role,
    'kelas_id' => $request->kelas_id,
]);

logger('HASIL CREATE:', [$result]);

    
        return redirect()->route('admin.users.index')->with('success','User berhasil ditambahkan');
    }
    
    

    public function edit(User $user) 
    { 
        $kelas = Kelas::all(); 
        return view('admin.users.edit', compact('user','kelas')); 
    }

    public function update(Request $request, User $user) 
    {
        $request->validate([
            'nama'=>'required|string',
            'email'=>'required|email|unique:users,email,'.$user->id,
            'role'=>'required|in:admin,kurikulum,siswa,kepala-sekolah',
            'kelas_id'=>'nullable|exists:kelas,id'
        ]);

        $data = [
            'nama'     => $request->nama,
            'email'    => $request->email,
            'role'     => $request->role,
            'kelas_id' => $request->kelas_id,
        ];

        if ($request->password) {
            $data['password'] = Hash::make($request->password);
        }

        $user->update($data);

        return redirect()->route('admin.users.index')->with('success','User berhasil diperbarui');
    }

    public function destroy(User $user) 
    { 
        $user->delete(); 
        return redirect()->route('admin.users.index')->with('success','User berhasil dihapus'); 
    }

    public function import(Request $request)
    {
        $request->validate([
            'file' => 'required|mimes:xlsx,xls'
        ]);

        Excel::import(new UserImport, $request->file('file'));

        return back()->with('success', 'Data user berhasil diimport!');
    }
}
