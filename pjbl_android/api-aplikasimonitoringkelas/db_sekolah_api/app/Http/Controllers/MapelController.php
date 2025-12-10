<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Mapel;
use Maatwebsite\Excel\Facades\Excel;
use App\Imports\MapelImport;

class MapelController extends Controller
{
    public function index()
    {
        $mapel = Mapel::all();
        return view('admin.mapel.index', compact('mapel'));
    }

    public function store(Request $request)
    {
        $request->validate([
            'nama_mapel' => 'required'
        ]);

        Mapel::create([
            'nama_mapel' => $request->nama_mapel
        ]);

        return redirect()->back()->with('success', 'Mapel berhasil ditambahkan');
    }

    public function edit($id)
    {
        $mapel = Mapel::findOrFail($id);
        return view('admin.mapel.edit', compact('mapel'));
    }

    public function update(Request $request, $id)
    {
        $request->validate([
            'nama_mapel' => 'required'
        ]);

        $mapel = Mapel::findOrFail($id);

        $mapel->update([
            'nama_mapel' => $request->nama_mapel
        ]);

        return redirect()->route('mapel.index')->with('success', 'Mapel diperbarui');
    }

    public function destroy($id)
    {
        Mapel::destroy($id);
        return redirect()->back()->with('success', 'Mapel berhasil dihapus');
    }

    // ============================
    // IMPORT EXCEL MAPEL
    // ============================
    public function importExcel(Request $request)
    {
        $request->validate([
            'file' => 'required|mimes:xlsx,xls'
        ]);

        try {
            Excel::import(new MapelImport, $request->file('file'));
            return back()->with('success', 'Data Mapel berhasil diimport!');
        } catch (\Exception $e) {
            return back()->with('error', 'Terjadi kesalahan saat import: ' . $e->getMessage());
        }
    }
}
