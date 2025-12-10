@extends('layouts.app')

@section('content')

<div class="container mt-4">

    <h3 class="fw-bold mb-3">Edit Ijin Guru</h3>

    <div class="card shadow-sm">
        <div class="card-body">

            <form action="{{ route('admin.ijin.update', $data->id) }}" method="POST">
                @csrf
                @method('PUT')

                {{-- Guru --}}
                <div class="mb-3">
                    <label class="form-label">Guru</label>
                    <select name="guru_id" class="form-select" required>
                        @foreach ($guru as $g)
                            <option value="{{ $g->id_guru }}"
                                {{ $data->guru_id == $g->id_guru ? 'selected' : '' }}>
                                {{ $g->nama_guru }}
                            </option>
                        @endforeach
                    </select>
                </div>

                {{-- Status --}}
                <div class="mb-3">
                    <label class="form-label">Status Ijin</label>
                    <select name="status" class="form-select" required>
                        <option value="sakit" {{ $data->status == 'sakit' ? 'selected' : '' }}>Sakit</option>
                        <option value="ijin" {{ $data->status == 'ijin' ? 'selected' : '' }}>Ijin</option>
                    </select>
                </div>

                {{-- Tanggal Mulai --}}
                <div class="mb-3">
                    <label class="form-label">Tanggal Mulai</label>
                    <input type="date" id="tanggal_mulai" name="tanggal_mulai" class="form-control"
                        value="{{ $data->tanggal_mulai }}" required>
                </div>

                {{-- Tanggal Selesai --}}
                <div class="mb-3">
                    <label class="form-label">Tanggal Selesai</label>
                    <input type="date" id="tanggal_selesai" name="tanggal_selesai" class="form-control"
                        value="{{ $data->tanggal_selesai }}" required>
                </div>

                {{-- Total Hari --}}
                <div class="mb-3">
                    <label class="form-label">Total Hari</label>
                    <input type="text" id="total_hari" class="form-control" readonly>
                </div>

                {{-- Keterangan --}}
                <div class="mb-3">
                    <label class="form-label">Keterangan</label>
                    <textarea name="keterangan" class="form-control" rows="3">{{ $data->keterangan }}</textarea>
                </div>

                <button class="btn btn-primary">Update</button>
                <a href="{{ route('admin.ijin.index') }}" class="btn btn-secondary">Kembali</a>

            </form>

        </div>
    </div>

</div>

{{-- Script menghitung total hari --}}
<script>
function hitungHari() {
    let mulai = document.getElementById('tanggal_mulai').value;
    let selesai = document.getElementById('tanggal_selesai').value;

    if (mulai && selesai) {
        let d1 = new Date(mulai);
        let d2 = new Date(selesai);

        let diff = Math.floor((d2 - d1) / (1000 * 60 * 60 * 24)) + 1;

        if (diff > 0) {
            document.getElementById('total_hari').value = diff + " Hari";
        } else {
            document.getElementById('total_hari').value = "Tanggal tidak valid";
        }
    }
}

// Event listener ketika tanggal diubah
document.getElementById('tanggal_mulai').addEventListener('change', hitungHari);
document.getElementById('tanggal_selesai').addEventListener('change', hitungHari);

// Hitung otomatis ketika halaman pertama kali dibuka
window.onload = hitungHari;
</script>

@endsection
