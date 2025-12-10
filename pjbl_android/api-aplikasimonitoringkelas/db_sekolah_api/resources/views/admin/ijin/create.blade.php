@extends('layouts.app')

@section('content')

<div class="container mt-4">

    <h3 class="fw-bold mb-3">Tambah Ijin Guru</h3>

    <div class="card shadow-sm">
        <div class="card-body">

            <form action="{{ route('ijin.store') }}" method="POST">
                @csrf

                {{-- Pilih Guru --}}
                <div class="mb-3">
                    <label class="form-label">Guru</label>
                    <select name="guru_id" id="guru_select" class="form-select" required>
                        <option value="">-- Pilih Guru --</option>
                        @foreach ($guru as $g)
                            <option value="{{ $g->id_guru }}" data-nama="{{ $g->nama_guru }}">
                                {{ $g->nama_guru }}
                            </option>
                        @endforeach
                    </select>
                </div>

                {{-- Hidden Nama Guru --}}
                <input type="hidden" name="nama_guru" id="nama_guru">

                {{-- Status Ijin --}}
                <div class="mb-3">
                    <label class="form-label">Status Ijin</label>
                    <select name="status" class="form-select" required>
                        <option value="sakit">Sakit</option>
                        <option value="ijin">Ijin</option>
                    </select>
                </div>

                {{-- Tanggal Mulai --}}
                <div class="mb-3">
                    <label class="form-label">Tanggal Mulai</label>
                    <input type="date" name="tanggal_mulai" id="tanggal_mulai" class="form-control" required>
                </div>

                {{-- Tanggal Selesai --}}
                <div class="mb-3">
                    <label class="form-label">Tanggal Selesai</label>
                    <input type="date" name="tanggal_selesai" id="tanggal_selesai" class="form-control" required>
                </div>

                {{-- Total Hari (readonly) --}}
                <div class="mb-3">
                    <label class="form-label">Total Hari</label>
                    <input type="text" id="total_hari" class="form-control" readonly>
                </div>

                {{-- Keterangan --}}
                <div class="mb-3">
                    <label class="form-label">Keterangan</label>
                    <textarea name="keterangan" class="form-control" rows="3"></textarea>
                </div>

                <button class="btn btn-primary">Simpan</button>
                <a href="{{ route('ijin.index') }}" class="btn btn-secondary">Kembali</a>

            </form>

        </div>
    </div>

</div>

{{-- Script otomatis mengisi nama guru --}}
<script>
    document.getElementById('guru_select').addEventListener('change', function () {
        let nama = this.options[this.selectedIndex].getAttribute('data-nama');
        document.getElementById('nama_guru').value = nama;
    });
</script>

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

document.getElementById('tanggal_mulai').addEventListener('change', hitungHari);
document.getElementById('tanggal_selesai').addEventListener('change', hitungHari);
</script>

@endsection
