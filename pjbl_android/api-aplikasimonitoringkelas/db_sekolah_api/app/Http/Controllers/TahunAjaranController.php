<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\TahunAjaran;
use Maatwebsite\Excel\Facades\Excel;
use App\Imports\TahunAjaranImport;

class TahunAjaranController extends Controller
{
    public function index() 
    { 
        $tahunAjaran = TahunAjaran::all(); 
        return view('admin.tahun_ajaran.index', compact('tahunAjaran')); 
    }

    public function create() 
    { 
        return view('admin.tahun_ajaran.create'); 
    }

    public function store(Request $request) 
    {
        $request->validate([
            'tahun' => 'required|string|unique:tahun_ajaran'
        ]);

        TahunAjaran::create([
            'tahun' => $request->tahun
        ]);

        return redirect()->route('tahun-ajaran.index')
            ->with('success', 'Tahun ajaran berhasil ditambahkan');
    }

    public function edit($id) 
    { 
        $tahun = TahunAjaran::findOrFail($id);
        return view('admin.tahun_ajaran.edit', compact('tahun')); 
    }

    public function update(Request $request, $id) 
    {
        $request->validate([
            'tahun' => 'required|string|unique:tahun_ajaran,tahun,' . $id
        ]);

        $tahun = TahunAjaran::findOrFail($id);

        $tahun->update([
            'tahun' => $request->tahun
        ]);

        return redirect()->route('tahun-ajaran.index')
            ->with('success', 'Tahun ajaran berhasil diperbarui');
    }

    public function destroy($id) 
    { 
        $tahun = TahunAjaran::findOrFail($id);
        $tahun->delete();

        return redirect()->route('tahun-ajaran.index')
            ->with('success', 'Tahun ajaran berhasil dihapus'); 
    }

    public function import(Request $request)
    {
        $request->validate([
            'file' => 'required|mimes:xls,xlsx'
        ]);

        try {
            Excel::import(new TahunAjaranImport, $request->file('file'));

            return back()->with('success', 'Data tahun ajaran berhasil diimport!');
        } catch (\Exception $e) {
            return back()->with('error', 'Terjadi kesalahan: ' . $e->getMessage());
        }
    }
}
