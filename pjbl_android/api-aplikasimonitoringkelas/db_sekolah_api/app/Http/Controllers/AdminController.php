<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Mapel;
use App\Models\Guru;
use App\Models\User;
use App\Models\Jadwal;
use Illuminate\Support\Facades\Hash;

class AdminController extends Controller
{
    public function __construct()
    {
        // Hanya admin yang bisa mengakses
        $this->middleware(['auth', 'admin']);
    }

    // ================= DASHBOARD / HOME =================
    public function index()
    {
        return view('home');
    }

    // ================== MAPEL ===================
    public function mapel()
    {
        $mapel = Mapel::all();
        return view('mapel', compact('mapel'));
    }

    public function storeMapel(Request $request)
    {
        $request->validate([
            'nama_mapel' => 'required|string|max:255'
        ]);

        Mapel::create([
            'nama_mapel' => $request->nama_mapel
        ]);

        return back()->with('success', 'Mapel berhasil ditambahkan!');
    }

    public function updateMapel(Request $request, $id)
    {
        $mapel = Mapel::findOrFail($id);

        $request->validate([
            'nama_mapel' => 'required|string|max:255'
        ]);

        $mapel->update([
            'nama_mapel' => $request->nama_mapel
        ]);

        return back()->with('success', 'Mapel berhasil diperbarui!');
    }

    public function deleteMapel($id)
    {
        $mapel = Mapel::findOrFail($id);
        $mapel->delete();

        return back()->with('success', 'Mapel berhasil dihapus!');
    }

    // ================== GURU ===================
    public function guru()
    {
        $guru = Guru::with('mapel')->get();
        $mapel = Mapel::all();
        return view('guru', compact('guru', 'mapel'));
    }

    public function storeGuru(Request $request)
    {
        $request->validate([
            'nama_guru' => 'required|string|max:255',
            'mapel_id' => 'required|exists:mapel,id'
        ]);

        Guru::create([
            'nama_guru' => $request->nama_guru,
            'mapel_id' => $request->mapel_id
        ]);

        return back()->with('success', 'Guru berhasil ditambahkan!');
    }

    public function updateGuru(Request $request, $id)
    {
        $guru = Guru::findOrFail($id);

        $request->validate([
            'nama_guru' => 'required|string|max:255',
            'mapel_id' => 'required|exists:mapel,id'
        ]);

        $guru->update([
            'nama_guru' => $request->nama_guru,
            'mapel_id' => $request->mapel_id
        ]);

        return back()->with('success', 'Guru berhasil diperbarui!');
    }

    public function deleteGuru($id)
    {
        $guru = Guru::findOrFail($id);
        $guru->delete();

        return back()->with('success', 'Guru berhasil dihapus!');
    }

    // ================== USER ===================
    public function users()
    {
        $users = User::all();
        return view('users', compact('users'));
    }

    public function storeUser(Request $request)
    {
        $request->validate([
            'nama' => 'required|string|max:255',
            'email' => 'required|email|unique:users',
            'password' => 'required|string|min:6',
            'role' => 'required|in:admin,kurikulum,siswa,kepala-sekolah'
        ]);

        User::create([
            'nama' => $request->nama,
            'email' => $request->email,
            'password' => Hash::make($request->password),
            'role' => $request->role
        ]);

        return back()->with('success', 'User berhasil ditambahkan!');
    }

    public function updateUser(Request $request, $id)
    {
        $user = User::findOrFail($id);

        $request->validate([
            'nama' => 'sometimes|required|string|max:255',
            'email' => 'sometimes|required|email|unique:users,email,' . $id,
            'password' => 'sometimes|required|string|min:6',
            'role' => 'sometimes|required|in:admin,kurikulum,siswa,kepala-sekolah'
        ]);

        $data = $request->all();

        if (isset($data['password'])) {
            $data['password'] = Hash::make($data['password']);
        }

        $user->update($data);

        return back()->with('success', 'User berhasil diperbarui!');
    }

    public function deleteUser($id)
    {
        $user = User::findOrFail($id);
        $user->delete();

        return back()->with('success', 'User berhasil dihapus!');
    }

    // ================== JADWAL ===================
    public function jadwal()
    {
        $jadwal = Jadwal::all();
        return view('jadwal', compact('jadwal'));
    }

    public function storeJadwal(Request $request)
    {
        $request->validate([
            'mapel_id' => 'required|exists:mapel,id',
            'guru_id' => 'required|exists:guru,id',
            'hari' => 'required|string',
            'jam_mulai' => 'required',
            'jam_selesai' => 'required',
            'kelas' => 'required|in:10,11,12',
        ]);
    
        Jadwal::create($request->all());
    
        return back()->with('success', 'Jadwal berhasil ditambahkan!');
    }

    public function updateJadwal(Request $request, $id)
    {
        $jadwal = Jadwal::findOrFail($id);

        $request->validate([
            'mapel_id' => 'required|exists:mapel,id',
            'guru_id' => 'required|exists:guru,id',
            'hari' => 'required|string',
            'jam_mulai' => 'required',
            'jam_selesai' => 'required'
        ]);

        $jadwal->update($request->all());

        return back()->with('success', 'Jadwal berhasil diperbarui!');
    }

    public function deleteJadwal($id)
    {
        $jadwal = Jadwal::findOrFail($id);
        $jadwal->delete();

        return back()->with('success', 'Jadwal berhasil dihapus!');
    }
}
