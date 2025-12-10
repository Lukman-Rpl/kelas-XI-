<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Jurusan;
use Maatwebsite\Excel\Facades\Excel;
use App\Imports\JurusanImport;

class JurusanController extends Controller
{
    public function index()
    {
        $jurusan = Jurusan::all();
        return view('admin.jurusan.index', compact('jurusan'));
    }

    public function store(Request $request)
    {
        $request->validate([
            'nama_jurusan' => 'required|string|unique:jurusan'
        ]);

        Jurusan::create([
            'nama_jurusan' => $request->nama_jurusan
        ]);

        return redirect()->route('admin.jurusan.index')
            ->with('success', 'Jurusan berhasil ditambahkan');
    }

    public function edit($id)
    {
        $jurusan = Jurusan::findOrFail($id);
        return view('admin.jurusan.edit', compact('jurusan'));
    }

    public function update(Request $request, $id)
    {
        $jurusan = Jurusan::findOrFail($id);

        $request->validate([
            'nama_jurusan' => 'required|string|unique:jurusan,nama_jurusan,' . $jurusan->id
        ]);

        $jurusan->update([
            'nama_jurusan' => $request->nama_jurusan
        ]);

        return redirect()->route('admin.jurusan.index')
            ->with('success', 'Jurusan berhasil diperbarui');
    }

    public function destroy($id)
    {
        $jurusan = Jurusan::findOrFail($id);
        $jurusan->delete();

        return redirect()->route('admin.jurusan.index')
            ->with('success', 'Jurusan berhasil dihapus');
    }

    public function import(Request $request)
    {
        $request->validate([
            'file' => 'required|mimes:xls,xlsx'
        ]);

        try {
            Excel::import(new JurusanImport, $request->file('file'));

            return back()->with('success', 'Data jurusan berhasil diimport!');
        } catch (\Exception $e) {
            return back()->with('error', 'Terjadi kesalahan: ' . $e->getMessage());
        }
    }
}
