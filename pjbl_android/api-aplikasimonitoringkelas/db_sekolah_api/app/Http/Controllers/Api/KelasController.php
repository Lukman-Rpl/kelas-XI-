<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Kelas;

class KelasController extends Controller
{
    /**
     * GET /kelas
     * List semua kelas
     */
    public function index()
    {
        $kelas = Kelas::with(['jurusan', 'tahunAjaran'])
            ->orderBy('id', 'DESC')
            ->get();

        return response()->json([
            'success' => true,
            'message' => 'List data kelas',
            'data' => $kelas
        ], 200);
    }

    /**
     * GET /kelas/{id}
     * Detail kelas
     */
    public function show($id)
    {
        $kelas = Kelas::with(['jurusan', 'tahunAjaran'])->find($id);

        if (!$kelas) {
            return response()->json([
                'success' => false,
                'message' => 'Kelas tidak ditemukan',
                'data' => null
            ], 404);
        }

        return response()->json([
            'success' => true,
            'message' => 'Detail kelas',
            'data' => $kelas
        ], 200);
    }

    /**
     * POST /kelas
     * Tambah kelas baru
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'nama_kelas' => 'required|string',
            'jurusan_id' => 'required|exists:jurusan,id',
            'tahun_ajaran_id' => 'required|exists:tahun_ajaran,id'
        ]);

        $kelas = Kelas::create($validated);

        return response()->json([
            'success' => true,
            'message' => 'Kelas berhasil ditambahkan',
            'data' => $kelas
        ], 201);
    }

    /**
     * PUT /kelas/{id}
     * Update data kelas
     */
    public function update(Request $request, $id)
    {
        $kelas = Kelas::find($id);

        if (!$kelas) {
            return response()->json([
                'success' => false,
                'message' => 'Kelas tidak ditemukan',
                'data' => null
            ], 404);
        }

        $validated = $request->validate([
            'nama_kelas' => 'required|string',
            'jurusan_id' => 'required|exists:jurusan,id',
            'tahun_ajaran_id' => 'required|exists:tahun_ajaran,id'
        ]);

        $kelas->update($validated);

        return response()->json([
            'success' => true,
            'message' => 'Kelas berhasil diperbarui',
            'data' => $kelas
        ], 200);
    }

    /**
     * DELETE /kelas/{id}
     * Hapus kelas
     */
    public function destroy($id)
    {
        $kelas = Kelas::find($id);

        if (!$kelas) {
            return response()->json([
                'success' => false,
                'message' => 'Kelas tidak ditemukan',
                'data' => null
            ], 404);
        }

        $kelas->delete();

        return response()->json([
            'success' => true,
            'message' => 'Kelas berhasil dihapus',
            'data' => null
        ], 200);
    }
}
