<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Ijin;
use App\Models\Guru;
use Maatwebsite\Excel\Facades\Excel;
use App\Imports\IjinImport;
use Carbon\Carbon;


class IjinController extends Controller
{
    /**
     * Menampilkan daftar ijin
     */
    public function index()
    {
        $data = Ijin::with('guru')->orderBy('id', 'DESC')->get();
        return view('admin.ijin.index', compact('data'));
    }

    /**
     * Form tambah ijin
     */
    public function create()
{
    $guru = Guru::orderBy('nama_guru')->get(); // <- perbaikan
    return view('admin.ijin.create', compact('guru'));
}


    /**
     * Simpan ijin baru
     */
    public function store(Request $request)
    {
        $request->validate([
            'guru_id'          => 'required|exists:guru,id_guru',
            'status'           => 'required|in:sakit,ijin',
            'tanggal_mulai'    => 'required|date',
            'tanggal_selesai'  => 'required|date|after_or_equal:tanggal_mulai',
            'keterangan'       => 'nullable|string',
        ]);
    
        $guru = Guru::find($request->guru_id);
    
        // HITUNG TOTAL HARI
        $start = Carbon::parse($request->tanggal_mulai);
        $end   = Carbon::parse($request->tanggal_selesai);
        $hari  = $start->diffInDays($end) + 1;
    
        Ijin::create([
            'guru_id'          => $request->guru_id,
            'nama_guru'        => $guru->nama_guru,
            'status'           => $request->status,
            'tanggal_mulai'    => $request->tanggal_mulai,
            'tanggal_selesai'  => $request->tanggal_selesai,
            'hari'             => $hari, // SIMPAN KE DATABASE
            'keterangan'       => $request->keterangan,
        ]);
    
        return redirect()->route('admin.ijin.index')
                         ->with('success', 'Data ijin berhasil ditambahkan.');
    }
    

    /**
     * Form edit ijin
     */
    public function edit($id)
    {
        $data = Ijin::findOrFail($id);
        $guru = Guru::orderBy('nama')->get();

        return view('admin.ijin.edit', compact('data', 'guru'));
    }

    /**
     * Update ijin
     */
    public function update(Request $request, $id)
    {
        $request->validate([
            'guru_id'          => 'required|exists:guru,id_guru',
            'status'           => 'required|in:sakit,ijin',
            'tanggal_mulai'    => 'required|date',
            'tanggal_selesai'  => 'required|date|after_or_equal:tanggal_mulai',
            'keterangan'       => 'nullable|string',
        ]);
    
        $ijin = Ijin::findOrFail($id);
        $guru = Guru::find($request->guru_id);
    
        // HITUNG ULANG TOTAL HARI
        $start = Carbon::parse($request->tanggal_mulai);
        $end   = Carbon::parse($request->tanggal_selesai);
        $hari  = $start->diffInDays($end) + 1;
    
        $ijin->update([
            'guru_id'          => $request->guru_id,
            'nama_guru'        => $guru->nama_guru,
            'status'           => $request->status,
            'tanggal_mulai'    => $request->tanggal_mulai,
            'tanggal_selesai'  => $request->tanggal_selesai,
            'hari'             => $hari, // UPDATE JUMLAH HARI
            'keterangan'       => $request->keterangan,
        ]);
    
        return redirect()->route('admin.ijin.index')
                         ->with('success', 'Data ijin berhasil diperbarui.');
    }
    


    /**
     * ============================
     *  IMPORT IJIN EXCEL
     * ============================
     */
    public function import(Request $request)
    {
        $request->validate([
            'file' => 'required|mimes:xls,xlsx'
        ]);

        try {
            Excel::import(new IjinImport, $request->file('file'));
            return back()->with('success', 'Data ijin berhasil diimport!');
        } catch (\Exception $e) {
            return back()->with('error', 'Terjadi kesalahan saat import: ' . $e->getMessage());
        }
    }
}