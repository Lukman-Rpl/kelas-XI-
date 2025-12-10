<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Guru;
use App\Models\Mapel;
use Maatwebsite\Excel\Facades\Excel;
use App\Imports\GuruImport;

class GuruController extends Controller
{
    // ✅ Tampilkan daftar guru
    public function index()
    {
        $guru = Guru::with('mapel')->get();
        $mapel = Mapel::all();

        return view('admin.guru.index', compact('guru', 'mapel'));
    }

    // ✅ Simpan Guru
    public function store(Request $request)
    {
        $request->validate([
            'nama_guru' => 'required|string',
            'mapel_id'  => 'required|exists:mapel,id'
        ]);

        Guru::create([
            'nama_guru' => $request->nama_guru,
            'mapel_id'  => $request->mapel_id,
            'nip'       => $request->nip,
        ]);

        return redirect()->back()->with('success', 'Guru berhasil ditambahkan');
    }

    // ✅ Edit Guru
    public function edit($id)
    {
        $guru = Guru::findOrFail($id);
        $mapel = Mapel::all();

        return view('admin.guru.edit', compact('guru', 'mapel'));
    }

    // ✅ Update Guru
    public function update(Request $request, $id)
    {
        $request->validate([
            'nama_guru' => 'required|string',
            'mapel_id'  => 'required|exists:mapel,id'
        ]);

        $guru = Guru::findOrFail($id);

        $guru->update([
            'nama_guru' => $request->nama_guru,
            'mapel_id'  => $request->mapel_id,
            'nip'       => $request->nip,
        ]);

        return redirect()->route('guru.index')->with('success', 'Guru berhasil diperbarui');
    }

    // ✅ Hapus Guru
    public function destroy($id)
    {
        Guru::destroy($id);
        return redirect()->back()->with('success', 'Guru berhasil dihapus');
    }

    // ✅ IMPORT EXCEL
    public function importExcel(Request $request)
    {
        $request->validate([
            'file' => 'required|mimes:xlsx,xls'
        ]);

        try {
            Excel::import(new GuruImport, $request->file('file'));
            return back()->with('success', 'Data Guru berhasil diimport!');
        } catch (\Exception $e) {
            return back()->with('error', 'Gagal mengimport: ' . $e->getMessage());
        }
    }
}
