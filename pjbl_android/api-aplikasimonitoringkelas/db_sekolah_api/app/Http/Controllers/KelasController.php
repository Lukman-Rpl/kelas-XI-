<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Kelas;
use App\Models\Jurusan;
use App\Models\TahunAjaran;
use Maatwebsite\Excel\Facades\Excel;
use App\Imports\KelasImport;

class KelasController extends Controller
{
    public function index()
    {
        $kelas = Kelas::with('jurusan', 'tahunAjaran')->get();
        return view('admin.kelas.index', compact('kelas'));
    }

    public function create()
    {
        $jurusan = Jurusan::all();
        $tahun = TahunAjaran::all();
        return view('admin.kelas.create', compact('jurusan','tahun'));
    }

    public function store(Request $request)
    {
        $request->validate([
            'nama_kelas' => 'required|string',
            'jurusan_id' => 'required|exists:jurusan,id',
            'tahun_ajaran_id' => 'required|exists:tahun_ajaran,id'
        ]);

        Kelas::create($request->all());
        return redirect()->route('admin.kelas.index')->with('success','Kelas berhasil ditambahkan');
    }

    public function edit(Kelas $kelas)
    {
        $jurusan = Jurusan::all();
        $tahun = TahunAjaran::all();
        return view('admin.kelas.edit', compact('kelas','jurusan','tahun'));
    }

    public function update(Request $request, Kelas $kelas)
    {
        $request->validate([
            'nama_kelas' => 'required|string',
            'jurusan_id' => 'required|exists:jurusan,id',
            'tahun_ajaran_id' => 'required|exists:tahun_ajaran,id'
        ]);

        $kelas->update($request->all());
        return redirect()->route('admin.kelas.index')->with('success','Kelas berhasil diperbarui');
    }

    public function destroy(Kelas $kelas)
    {
        $kelas->delete();
        return redirect()->route('admin.kelas.index')->with('success','Kelas berhasil dihapus');
    }

    /**
     * Import Excel
     */
    public function import(Request $request)
    {
        $request->validate([
            'file' => 'required|mimes:xls,xlsx'
        ]);

        try {
            Excel::import(new KelasImport, $request->file('file'));

            return back()->with('success', 'Data kelas berhasil diimport!');
        } catch (\Exception $e) {
            return back()->with('error', 'Terjadi kesalahan: ' . $e->getMessage());
        }
    }
}
