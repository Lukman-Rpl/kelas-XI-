<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Jadwal;
use App\Models\Kelas;
use App\Models\Guru;
use App\Models\Mapel;
use Maatwebsite\Excel\Facades\Excel;
use App\Imports\JadwalImport;

class JadwalController extends Controller
{
    public function index()
    {
        $jadwal = Jadwal::with(['kelas', 'guru'])->get();
        $kelas  = Kelas::all();
        $guru   = Guru::all();
        $mapel  = Mapel::all();

        return view('admin.jadwal.index', compact('jadwal', 'kelas', 'guru', 'mapel'));
    }

    public function create()
    {
        $kelas = Kelas::all();
        $guru  = Guru::all();
        $mapel = Mapel::all();

        return view('admin.jadwal.create', compact('kelas', 'guru', 'mapel'));
    }

    public function store(Request $request)
    {
        $request->validate([
            'hari'      => 'required|string',
            'tanggal'   => 'required|date',
            'kelas_id'  => 'required|exists:kelas,id',
            'jam_ke'    => 'required|string',
            'sampai_jam'=> 'required|string',
            'mapel'     => 'required|string',
            'guru_id'   => 'required|exists:guru,id_guru',
            'status'    => 'required|in:hadir,tidak_hadir,terlambat',
            'keterangan'=> 'nullable|string'
        ]);

        Jadwal::create($request->all());

        return redirect()->route('jadwal.index')->with('success', 'Jadwal berhasil ditambahkan');
    }

    public function edit(Jadwal $jadwal)
    {
        $kelas = Kelas::all();
        $guru  = Guru::all();
        $mapel = Mapel::all();

        return view('admin.jadwal.edit', compact('jadwal', 'kelas', 'guru', 'mapel'));
    }

    public function update(Request $request, Jadwal $jadwal)
    {
        $request->validate([
            'hari'      => 'required|string',
            'tanggal'   => 'required|date',
            'kelas_id'  => 'required|exists:kelas,id',
            'jam_ke'    => 'required|string',
            'sampai_jam'=> 'required|string',
            'mapel'     => 'required|string',
            'guru_id'   => 'required|exists:guru,id_guru',
            'status'    => 'required|in:hadir,tidak_hadir,terlambat',
            'keterangan'=> 'nullable|string'
        ]);

        $jadwal->update($request->all());

        return redirect()->route('jadwal.index')->with('success', 'Jadwal berhasil diperbarui');
    }

    public function destroy(Jadwal $jadwal)
    {
        $jadwal->delete();
        return redirect()->route('jadwal.index')->with('success', 'Jadwal berhasil dihapus');
    }

    public function import(Request $request)
    {
        $request->validate([
            'file' => 'required|mimes:xls,xlsx'
        ]);

        try {
            Excel::import(new JadwalImport, $request->file('file'));

            return back()->with('success', 'Data jadwal berhasil diimport!');
        } catch (\Exception $e) {
            return back()->with('error', 'Gagal mengimpor data: ' . $e->getMessage());
        }
    }
}
