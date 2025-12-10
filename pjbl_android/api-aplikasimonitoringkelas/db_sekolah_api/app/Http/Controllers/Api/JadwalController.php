<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Jadwal;
use App\Models\Mapel;
use Illuminate\Support\Facades\Auth;

class JadwalController extends Controller
{
    /**
     * GET /jadwal
     * Selalu mengembalikan array (meskipun hanya 1 data)
     */
    public function index(Request $request)
    {
        $user = Auth::user();

        $query = Jadwal::with(['mapel', 'guru', 'pengganti']);

        // Jika siswa dan tidak mengirim kelas_id, otomatis berdasarkan kelas siswa
        if ($user?->role === 'siswa' && !$request->filled('kelas_id')) {
            $query->where('kelas_id', $user->kelas_id);
        }

        // Filter lain
        if ($request->filled('kelas_id')) $query->where('kelas_id', $request->kelas_id);
        if ($request->filled('hari')) $query->where('hari', $request->hari);
        if ($request->filled('tanggal')) $query->where('tanggal', $request->tanggal);
        if ($request->filled('status')) $query->where('status', $request->status);

        // Ambil SEMUA jadwal sebagai array
        $items = $query->orderBy('kelas_id')
            ->orderBy('jam_ke')
            ->get();

        return response()->json([
            'success' => true,
            'message' => 'Daftar jadwal',
            'data' => $items->map(fn($item) => $this->formatJadwal($item)),
        ]);
    }

    /**
     * GET /jadwal/{id}
     * Mengembalikan satu object
     */
    public function show($id)
    {
        $item = Jadwal::with(['mapel', 'guru', 'pengganti'])->find($id);

        if (!$item) {
            return response()->json([
                'success' => false,
                'message' => 'Jadwal tidak ditemukan',
                'data' => null
            ], 404);
        }

        return response()->json([
            'success' => true,
            'message' => 'Jadwal ditemukan',
            'data' => $this->formatJadwal($item)
        ]);
    }

    /**
     * POST /jadwal
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'hari' => 'required|string',
            'tanggal' => 'required|date',
            'kelas_id' => 'required|integer',
            'jam_ke' => 'required|date_format:H:i:s',
            'sampai_jam' => 'required|date_format:H:i:s',
            'mapel_id' => 'sometimes|integer',
            'mapel' => 'sometimes|string',
            'guru_id' => 'required|integer',
            'guru_pengganti_id' => 'nullable|integer',
            'status' => 'required|string|in:hadir,tidak_hadir,terlambat',
            'keterangan' => 'nullable|string',
        ]);

        // Mapping mapel jika menggunakan nama mapel
        if (!isset($validated['mapel_id']) && isset($validated['mapel'])) {
            $mapelModel = Mapel::where('nama_mapel', $validated['mapel'])->first();
            if (!$mapelModel) {
                return response()->json([
                    'success' => false,
                    'message' => 'Mapel tidak ditemukan',
                    'data' => null
                ], 422);
            }
            $validated['mapel_id'] = $mapelModel->id;
        }

        $jadwal = Jadwal::create($validated);
        $jadwal->load(['mapel', 'guru', 'pengganti']);

        return response()->json([
            'success' => true,
            'message' => 'Jadwal berhasil ditambahkan',
            'data' => $this->formatJadwal($jadwal)
        ]);
    }

    /**
     * PUT /jadwal/{id}
     */
    public function update(Request $request, $id)
    {
        $jadwal = Jadwal::find($id);

        if (!$jadwal) {
            return response()->json([
                'success' => false,
                'message' => 'Jadwal tidak ditemukan',
                'data' => null
            ], 404);
        }

        $validated = $request->validate([
            'hari' => 'sometimes|string',
            'tanggal' => 'sometimes|date',
            'kelas_id' => 'sometimes|integer',
            'jam_ke' => 'sometimes|date_format:H:i:s',
            'sampai_jam' => 'sometimes|date_format:H:i:s',
            'mapel' => 'sometimes|string',
            'mapel_id' => 'sometimes|integer',
            'guru_id' => 'sometimes|integer',
            'guru_pengganti_id' => 'nullable|integer',
            'status' => 'sometimes|string|in:hadir,tidak_hadir,terlambat',
            'keterangan' => 'nullable|string',
        ]);

        $jadwal->update($validated);
        $jadwal->load(['mapel', 'guru', 'pengganti']);

        return response()->json([
            'success' => true,
            'message' => 'Jadwal berhasil diperbarui',
            'data' => $this->formatJadwal($jadwal)
        ]);
    }

    /**
     * DELETE /jadwal/{id}
     */
    public function destroy($id)
    {
        $jadwal = Jadwal::find($id);

        if (!$jadwal) {
            return response()->json([
                'success' => false,
                'message' => 'Jadwal tidak ditemukan',
                'data' => null
            ], 404);
        }

        $jadwal->delete();

        return response()->json([
            'success' => true,
            'message' => 'Jadwal berhasil dihapus',
            'data' => null
        ]);
    }

    /**
     * Format data jadwal biar konsisten
     */
    private function formatJadwal($item)
    {
        return [
            'id' => $item->id,
            'hari' => $item->hari,
            'tanggal' => $item->tanggal,
            'kelas_id' => $item->kelas_id,
            'jam_ke' => $item->jam_ke,
            'sampai_jam' => $item->sampai_jam,
            'mapel_id' => $item->mapel_id,
            'mapel' => $item->mapel?->nama_mapel ?? '-',
            'guru_id' => $item->guru_id,
            'guru' => $item->guru?->nama_guru ?? '-',
            'guru_pengganti_id' => $item->guru_pengganti_id,
            'guru_pengganti' => $item->pengganti?->nama_guru ?? '-',
            'status' => $item->status,
            'keterangan' => $item->keterangan ?? '-',
            'created_at' => $item->created_at?->toDateTimeString(),
            'updated_at' => $item->updated_at?->toDateTimeString(),
        ];
    }
}
