<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Mapel;

class MapelApiController extends Controller
{
    /**
     * GET /api/mapel
     * List semua mapel
     */
    public function index()
    {
        $mapel = Mapel::orderBy('id', 'DESC')->get();

        return response()->json([
            'success' => true,
            'message' => 'List data mapel',
            'data' => $mapel
        ], 200);
    }

    /**
     * POST /api/mapel
     * Tambah mapel baru
     */
    public function store(Request $request)
    {
        $request->validate([
            'nama_mapel' => 'required|string'
        ]);

        $mapel = Mapel::create([
            'nama_mapel' => $request->nama_mapel
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Mapel berhasil ditambahkan',
            'data' => $mapel
        ], 201);
    }

    /**
     * GET /api/mapel/{id}
     * Detail mapel
     */
    public function show($id)
    {
        $mapel = Mapel::find($id);

        if (!$mapel) {
            return response()->json([
                'success' => false,
                'message' => 'Mapel tidak ditemukan',
                'data' => null
            ], 404);
        }

        return response()->json([
            'success' => true,
            'message' => 'Detail mapel',
            'data' => $mapel
        ], 200);
    }

    /**
     * PUT /api/mapel/{id}
     * Update mapel
     */
    public function update(Request $request, $id)
    {
        $request->validate([
            'nama_mapel' => 'required|string'
        ]);

        $mapel = Mapel::find($id);

        if (!$mapel) {
            return response()->json([
                'success' => false,
                'message' => 'Mapel tidak ditemukan',
                'data' => null
            ], 404);
        }

        $mapel->update([
            'nama_mapel' => $request->nama_mapel
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Mapel berhasil diperbarui',
            'data' => $mapel
        ], 200);
    }

    /**
     * DELETE /api/mapel/{id}
     * Hapus mapel
     */
    public function destroy($id)
    {
        $mapel = Mapel::find($id);

        if (!$mapel) {
            return response()->json([
                'success' => false,
                'message' => 'Mapel tidak ditemukan',
                'data' => null
            ], 404);
        }

        $mapel->delete();

        return response()->json([
            'success' => true,
            'message' => 'Mapel berhasil dihapus',
            'data' => null
        ], 200);
    }
}
