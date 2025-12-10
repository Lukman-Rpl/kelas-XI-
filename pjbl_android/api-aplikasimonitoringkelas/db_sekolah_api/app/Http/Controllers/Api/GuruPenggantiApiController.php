<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\GuruPengganti;
use App\Models\Guru;
use Illuminate\Support\Facades\Validator;

class GuruPenggantiApiController extends Controller
{
    /**
     * GET /guru-pengganti
     * Ambil semua data guru pengganti
     */
    public function index()
    {
        $data = GuruPengganti::with(['guru', 'pengganti', 'mapel', 'kelas'])
            ->orderBy('id', 'DESC')
            ->get(); // <- dikembalikan sebagai ARRAY

        return response()->json([
            'success' => true,
            'message' => 'List data guru pengganti',
            'data' => $data
        ], 200);
    }

    /**
     * GET /guru-pengganti/{id}
     */
    public function show($id)
    {
        $data = GuruPengganti::with(['guru', 'pengganti', 'mapel', 'kelas'])->find($id);

        if (!$data) {
            return response()->json([
                'success' => false,
                'message' => 'Data guru pengganti tidak ditemukan',
                'data' => null
            ], 404);
        }

        return response()->json([
            'success' => true,
            'message' => 'Detail guru pengganti',
            'data' => $data
        ], 200);
    }

    /**
     * POST /guru-pengganti
     */
    public function store(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'guru_id'            => 'required|integer|exists:guru,id_guru',
            'guru_pengganti_id'  => 'required|integer|exists:guru,id_guru',
            'mapel_id'           => 'nullable|integer|exists:mapel,id',
            'kelas_id'           => 'nullable|integer|exists:kelas,id',
            'tanggal'            => 'required|date',
            'jam'                => 'nullable|string',
            'keterangan'         => 'nullable|string'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validasi gagal',
                'data' => $validator->errors()
            ], 422);
        }

        $pengganti = Guru::find($request->guru_pengganti_id);

        $data = GuruPengganti::create([
            'guru_id'           => $request->guru_id,
            'guru_pengganti_id' => $request->guru_pengganti_id,
            'nama'              => $pengganti?->nama_guru,
            'mapel_id'          => $request->mapel_id,
            'kelas_id'          => $request->kelas_id,
            'tanggal'           => $request->tanggal,
            'jam'               => $request->jam,
            'keterangan'        => $request->keterangan,
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Data guru pengganti berhasil ditambahkan',
            'data' => $data
        ], 201);
    }

    /**
     * PUT /guru-pengganti/{id}
     */
    public function update(Request $request, $id)
    {
        $data = GuruPengganti::find($id);

        if (!$data) {
            return response()->json([
                'success' => false,
                'message' => 'Data guru pengganti tidak ditemukan',
                'data' => null
            ], 404);
        }

        $validator = Validator::make($request->all(), [
            'guru_id'            => 'required|integer|exists:guru,id_guru',
            'guru_pengganti_id'  => 'required|integer|exists:guru,id_guru',
            'mapel_id'           => 'nullable|integer|exists:mapel,id',
            'kelas_id'           => 'nullable|integer|exists:kelas,id',
            'tanggal'            => 'required|date',
            'jam'                => 'nullable|string',
            'keterangan'         => 'nullable|string'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validasi gagal',
                'data' => $validator->errors()
            ], 422);
        }

        $pengganti = Guru::find($request->guru_pengganti_id);

        $data->update([
            'guru_id'           => $request->guru_id,
            'guru_pengganti_id' => $request->guru_pengganti_id,
            'nama'              => $pengganti?->nama_guru,
            'mapel_id'          => $request->mapel_id,
            'kelas_id'          => $request->kelas_id,
            'tanggal'           => $request->tanggal,
            'jam'               => $request->jam,
            'keterangan'        => $request->keterangan,
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Data guru pengganti berhasil diperbarui',
            'data' => $data
        ], 200);
    }

    /**
     * DELETE /guru-pengganti/{id}
     */
    public function destroy($id)
    {
        $data = GuruPengganti::find($id);

        if (!$data) {
            return response()->json([
                'success' => false,
                'message' => 'Data guru pengganti tidak ditemukan',
                'data' => null
            ], 404);
        }

        $data->delete();

        return response()->json([
            'success' => true,
            'message' => 'Data guru pengganti berhasil dihapus',
            'data' => null
        ], 200);
    }
}
