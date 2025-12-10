<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Guru;
use Illuminate\Support\Facades\Validator;

class GuruController extends Controller
{
    /**
     * GET /api/guru
     * Ambil semua guru
     */
    public function index()
    {
        $guruList = Guru::all();

        return response()->json([
            'success' => true,
            'message' => 'Data guru ditemukan',
            'data' => [
                'guru' => $guruList
            ]
        ], 200);
    }

    /**
     * GET /api/guru/{id}
     * Ambil 1 guru berdasarkan ID
     */
    public function show($id)
    {
        $guru = Guru::find($id);

        if (!$guru) {
            return response()->json([
                'success' => false,
                'message' => 'Guru tidak ditemukan',
                'data' => null
            ], 404);
        }

        return response()->json([
            'success' => true,
            'message' => 'Guru ditemukan',
            'data' => [
                'guru' => $guru
            ]
        ], 200);
    }

    /**
     * GET /api/guru/mapel/{id_mapel}
     * Cari guru berdasarkan ID mapel
     */
    public function getGuruByMapel($id_mapel)
    {
        $guru = Guru::where('id_mapel', $id_mapel)->first();

        if (!$guru) {
            return response()->json([
                'success' => false,
                'message' => 'Guru tidak ditemukan',
                'data' => null
            ], 404);
        }

        return response()->json([
            'success' => true,
            'message' => 'Guru ditemukan',
            'data' => [
                'guru' => $guru
            ]
        ], 200);
    }

    /**
     * POST /api/guru
     * Tambah data guru
     */
    public function store(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'nama_guru' => 'required|string|max:255',
            'nip'       => 'nullable|string|max:100',
            'id_mapel'  => 'nullable|integer|exists:mapel,id',
            'telepon'   => 'nullable|string|max:50',
            'alamat'    => 'nullable|string'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validasi gagal',
                'errors' => $validator->errors()
            ], 422);
        }

        $guru = Guru::create($validator->validated());

        return response()->json([
            'success' => true,
            'message' => 'Guru berhasil ditambahkan',
            'data' => [
                'guru' => $guru
            ]
        ], 201);
    }

    /**
     * PUT /api/guru/{id}
     * Update data guru
     */
    public function update(Request $request, $id)
    {
        $guru = Guru::find($id);

        if (!$guru) {
            return response()->json([
                'success' => false,
                'message' => 'Guru tidak ditemukan',
                'data' => null
            ], 404);
        }

        $validator = Validator::make($request->all(), [
            'nama_guru' => 'required|string|max:255',
            'nip'       => 'nullable|string|max:100',
            'id_mapel'  => 'nullable|integer|exists:mapel,id',
            'telepon'   => 'nullable|string|max:50',
            'alamat'    => 'nullable|string'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validasi gagal',
                'errors' => $validator->errors()
            ], 422);
        }

        $guru->update($validator->validated());

        return response()->json([
            'success' => true,
            'message' => 'Guru berhasil diperbarui',
            'data' => [
                'guru' => $guru
            ]
        ], 200);
    }

    /**
     * DELETE /api/guru/{id}
     * Hapus guru
     */
    public function destroy($id)
    {
        $guru = Guru::find($id);

        if (!$guru) {
            return response()->json([
                'success' => false,
                'message' => 'Guru tidak ditemukan',
                'data' => null
            ], 404);
        }

        $guru->delete();

        return response()->json([
            'success' => true,
            'message' => 'Guru berhasil dihapus',
            'data' => null
        ], 200);
    }
}
