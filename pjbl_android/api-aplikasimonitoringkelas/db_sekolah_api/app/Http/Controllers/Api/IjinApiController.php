<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Ijin;
use App\Models\Guru;
use Maatwebsite\Excel\Facades\Excel;
use App\Imports\IjinImport;
use Carbon\Carbon;

class IjinApiController extends Controller
{
    /**
     * GET /ijin
     * Selalu mengembalikan array
     */
    public function index()
    {
        $items = Ijin::with('guru')->orderBy('id', 'DESC')->get();

        return response()->json([
            'success' => true,
            'message' => 'List data ijin',
            'data' => $items->map(fn($item) => $this->formatIjin($item))
        ], 200);
    }

    /**
     * POST /ijin
     * Membuat ijin baru
     */
    public function store(Request $request)
    {
        $request->validate([
            'guru_id'          => 'required|exists:guru,id',
            'status'           => 'required|in:sakit,ijin',
            'tanggal_mulai'    => 'required|date',
            'tanggal_selesai'  => 'required|date|after_or_equal:tanggal_mulai',
            'keterangan'       => 'nullable|string',
        ]);

        $guru = Guru::find($request->guru_id);

        $hari = Carbon::parse($request->tanggal_mulai)
                ->diffInDays(Carbon::parse($request->tanggal_selesai)) + 1;

        $ijin = Ijin::create([
            'guru_id'          => $request->guru_id,
            'nama_guru'        => $guru->nama_guru ?? $guru->nama ?? 'Tidak diketahui',
            'status'           => $request->status,
            'tanggal_mulai'    => $request->tanggal_mulai,
            'tanggal_selesai'  => $request->tanggal_selesai,
            'hari'             => $hari,
            'keterangan'       => $request->keterangan,
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Ijin berhasil ditambahkan',
            'data' => $this->formatIjin($ijin)
        ], 201);
    }

    /**
     * GET /ijin/{id}
     * Mengembalikan 1 object
     */
    public function show($id)
    {
        $ijin = Ijin::with('guru')->find($id);

        if (!$ijin) {
            return response()->json([
                'success' => false,
                'message' => 'Data tidak ditemukan',
                'data' => null
            ], 404);
        }

        return response()->json([
            'success' => true,
            'message' => 'Detail ijin',
            'data' => $this->formatIjin($ijin)
        ], 200);
    }

    /**
     * PUT /ijin/{id}
     * Update ijin
     */
    public function update(Request $request, $id)
    {
        $request->validate([
            'guru_id'          => 'required|exists:guru,id',
            'status'           => 'required|in:sakit,ijin',
            'tanggal_mulai'    => 'required|date',
            'tanggal_selesai'  => 'required|date|after_or_equal:tanggal_mulai',
            'keterangan'       => 'nullable|string',
        ]);

        $ijin = Ijin::find($id);

        if (!$ijin) {
            return response()->json([
                'success' => false,
                'message' => 'Data tidak ditemukan',
                'data' => null
            ], 404);
        }

        $guru = Guru::find($request->guru_id);

        $hari = Carbon::parse($request->tanggal_mulai)
                ->diffInDays(Carbon::parse($request->tanggal_selesai)) + 1;

        $ijin->update([
            'guru_id'         => $request->guru_id,
            'nama_guru'       => $guru->nama_guru ?? $guru->nama ?? 'Tidak diketahui',
            'status'          => $request->status,
            'tanggal_mulai'   => $request->tanggal_mulai,
            'tanggal_selesai' => $request->tanggal_selesai,
            'hari'            => $hari,
            'keterangan'      => $request->keterangan,
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Ijin berhasil diperbarui',
            'data' => $this->formatIjin($ijin)
        ], 200);
    }

    /**
     * DELETE /ijin/{id}
     * Hapus ijin
     */
    public function destroy($id)
    {
        $ijin = Ijin::find($id);

        if (!$ijin) {
            return response()->json([
                'success' => false,
                'message' => 'Data tidak ditemukan',
                'data' => null
            ], 404);
        }

        $ijin->delete();

        return response()->json([
            'success' => true,
            'message' => 'Ijin berhasil dihapus',
            'data' => null
        ], 200);
    }

    /**
     * Import excel
     */
    public function import(Request $request)
    {
        $request->validate([
            'file' => 'required|mimes:xls,xlsx'
        ]);

        try {
            Excel::import(new IjinImport, $request->file('file'));

            return response()->json([
                'success' => true,
                'message' => 'Import berhasil',
                'data' => null
            ]);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Gagal import: '.$e->getMessage(),
                'data' => null
            ], 500);
        }
    }

    /**
     * Format ijin agar konsisten ke Android
     */
    private function formatIjin($item)
    {
        return [
            'id'               => $item->id,
            'guru_id'          => $item->guru_id,
            'nama_guru'        => $item->nama_guru,
            'status'           => $item->status,
            'tanggal_mulai'    => $item->tanggal_mulai,
            'tanggal_selesai'  => $item->tanggal_selesai,
            'hari'             => $item->hari,
            'keterangan'       => $item->keterangan ?: '-',
            'created_at'       => $item->created_at?->toDateTimeString(),
            'updated_at'       => $item->updated_at?->toDateTimeString(),
        ];
    }
}
