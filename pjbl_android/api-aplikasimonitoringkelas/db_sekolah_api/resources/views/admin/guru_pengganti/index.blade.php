@extends('layouts.app')

@section('title', 'Data Guru Pengganti')

@section('content')
<div class="container">
    <h1>Data Guru Pengganti</h1>

    {{-- Notifikasi --}}
    @if(session('success'))
        <div class="alert alert-success">{{ session('success') }}</div>
    @endif
    @if(session('error'))
        <div class="alert alert-danger">{{ session('error') }}</div>
    @endif

    {{-- Tombol Tambah & Import --}}
    <div class="mb-3 d-flex align-items-center gap-2">
        <a href="{{ route('guru_pengganti.create') }}" class="btn btn-primary">Tambah Guru Pengganti</a>

        <form action="{{ route('guru_pengganti.import') }}" method="POST" enctype="multipart/form-data" class="d-flex gap-1">
            @csrf
            <input type="file" name="file" class="form-control form-control-sm" required>
            <button type="submit" class="btn btn-success btn-sm">Import Excel</button>
        </form>
    </div>

    {{-- Tabel Data --}}
    <div class="table-responsive">
        <table class="table table-bordered table-striped">
            <thead class="table-dark">
                <tr>
                    <th>#</th>
                    <th>Guru</th>
                    <th>Pengganti</th>
                    <th>Mapel</th>
                    <th>Kelas</th>
                    <th>Tanggal</th>
                    <th>Jam</th>
                    <th>Keterangan</th>
                    <th>Aksi</th>
                </tr>
            </thead>
            <tbody>
                @forelse($data as $item)
                <tr>
                    <td>{{ $loop->iteration }}</td>
                    <td>{{ $item->guru->nama_guru ?? '-' }}</td>
                    <td>{{ $item->nama ?? '-' }}</td>
                    <td>{{ $item->mapel->nama_mapel ?? '-' }}</td>
                    <td>{{ $item->kelas->nama_kelas ?? '-' }}</td>
                    <td>{{ $item->tanggal }}</td>
                    <td>{{ $item->jam }}</td>
                    <td>{{ $item->keterangan ?? '-' }}</td>
                    <td class="d-flex gap-1">
                        <a href="{{ route('guru_pengganti.edit', $item->id) }}" class="btn btn-warning btn-sm">Edit</a>
                        <form action="{{ route('guru_pengganti.destroy', $item->id) }}" method="POST" onsubmit="return confirm('Hapus data ini?')">
                            @csrf
                            @method('DELETE')
                            <button type="submit" class="btn btn-danger btn-sm">Hapus</button>
                        </form>
                    </td>
                </tr>
                @empty
                <tr>
                    <td colspan="9" class="text-center">Data Guru Pengganti belum tersedia.</td>
                </tr>
                @endforelse
            </tbody>
        </table>
    </div>
</div>
@endsection
