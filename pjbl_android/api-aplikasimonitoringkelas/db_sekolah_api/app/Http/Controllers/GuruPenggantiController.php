<?php

namespace App\Http\Controllers;

use App\Models\GuruPengganti;
use App\Models\Guru;
use App\Models\Mapel;
use App\Models\Kelas;
use Illuminate\Http\Request;
use Maatwebsite\Excel\Facades\Excel;
use App\Imports\GuruPenggantiImport;

class GuruPenggantiController extends Controller
{
    public function index()
    {
        $data = GuruPengganti::with(['guru', 'pengganti', 'mapel', 'kelas'])
            ->orderBy('id', 'DESC')
            ->get();

        return view('admin.guru_pengganti.index', compact('data'));
    }

    public function create()
    {
        $guru = Guru::all();
        $mapel = Mapel::all();
        $kelas = Kelas::all();

        return view('admin.guru_pengganti.create', compact('guru', 'mapel', 'kelas'));
    }

    public function store(Request $request)
    {
        $request->validate([
            'guru_id'            => 'required|exists:guru,id_guru',
            'guru_pengganti_id'  => 'required|exists:guru,id_guru',
            'mapel_id'           => 'required|exists:mapel,id',
            'kelas_id'           => 'required|exists:kelas,id',
            'tanggal'            => 'required|date',
            'jam'                => 'required|string',
            'keterangan'         => 'nullable|string'
        ]);

        // Ambil nama guru pengganti
        $pengganti = Guru::find($request->guru_pengganti_id);

        GuruPengganti::create([
            'guru_id'            => $request->guru_id,
            'guru_pengganti_id'  => $request->guru_pengganti_id,
            'nama'               => $pengganti ? $pengganti->nama_guru : null,
            'mapel_id'           => $request->mapel_id,
            'kelas_id'           => $request->kelas_id,
            'tanggal'            => $request->tanggal,
            'jam'                => $request->jam,
            'keterangan'         => $request->keterangan,
        ]);

        return redirect()->route('guru_pengganti.index')->with('success', 'Data berhasil ditambahkan');
    }

    public function edit($id)
    {
        $data = GuruPengganti::findOrFail($id);
        $guru = Guru::all();
        $mapel = Mapel::all();
        $kelas = Kelas::all();

        return view('admin.guru_pengganti.edit', compact('data','guru','mapel','kelas'));
    }

    public function update(Request $request, $id)
    {
        $request->validate([
            'guru_id'            => 'required|exists:guru,id_guru',
            'guru_pengganti_id'  => 'required|exists:guru,id_guru',
            'mapel_id'           => 'required|exists:mapel,id',
            'kelas_id'           => 'required|exists:kelas,id',
            'tanggal'            => 'required|date',
            'jam'                => 'required|string',
            'keterangan'         => 'nullable|string'
        ]);

        $data = GuruPengganti::findOrFail($id);
        $pengganti = Guru::find($request->guru_pengganti_id);

        $data->update([
            'guru_id'            => $request->guru_id,
            'guru_pengganti_id'  => $request->guru_pengganti_id,
            'nama'               => $pengganti ? $pengganti->nama_guru : null,
            'mapel_id'           => $request->mapel_id,
            'kelas_id'           => $request->kelas_id,
            'tanggal'            => $request->tanggal,
            'jam'                => $request->jam,
            'keterangan'         => $request->keterangan,
        ]);

        return redirect()->route('guru_pengganti.index')->with('success', 'Data berhasil diperbarui');
    }

    public function destroy($id)
    {
        GuruPengganti::destroy($id);

        return redirect()->route('guru_pengganti.index')->with('success', 'Data berhasil dihapus');
    }

    // ✅ IMPORT EXCEL
    public function importExcel(Request $request)
    {
        $request->validate([
            'file' => 'required|mimes:xlsx,xls'
        ]);

        try {
            Excel::import(new GuruPenggantiImport, $request->file('file'));
            return back()->with('success', 'Data Guru Pengganti berhasil diimport!');
        } catch (\Exception $e) {
            return back()->with('error', 'Gagal mengimport: ' . $e->getMessage());
        }
    }
}
