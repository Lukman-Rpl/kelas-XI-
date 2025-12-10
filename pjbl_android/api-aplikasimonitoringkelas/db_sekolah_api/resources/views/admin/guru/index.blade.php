@extends('layouts.app')
@section('title', 'Data Guru')

@section('content')
    <h2>👨‍🏫 Data Guru</h2>

    {{-- Form Tambah Guru --}}
    <form method="POST" action="{{ route('guru.store') }}" class="form-box">
        @csrf
        <input type="text" name="nama_guru" placeholder="Nama Guru" required>
        <input type="text" name="nip" placeholder="NIP (Opsional)">

        <select name="mapel_id" required>
            <option value="">-- Pilih Mapel --</option>
            @foreach($mapel as $m)
                <option value="{{ $m->id }}">{{ $m->nama_mapel }}</option>
            @endforeach
        </select>

        <button type="submit">Tambah Guru</button>
    </form>

    @if (session('success'))
        <div class="alert success">{{ session('success') }}</div>
    @endif

    {{-- Tabel Guru --}}
    <table class="table">
        <thead>
            <tr>
                <th>No</th>
                <th>Nama Guru</th>
                <th>NIP</th>
                <th>Mapel</th>
                <th>Aksi</th>
            </tr>
        </thead>

        <tbody>
            @foreach($guru as $i => $g)
                <tr>
                    <td>{{ $i + 1 }}</td>
                    <td>{{ $g->nama_guru }}</td>
                    <td>{{ $g->nip ?? '-' }}</td>
                    <td>{{ $g->mapel->nama_mapel ?? '-' }}</td>

                    <td>
                        {{-- Edit pakai id_guru --}}
                        <a href="{{ route('guru.edit', $g->id_guru) }}" class="btn-edit">Edit</a>

                        {{-- Delete pakai id_guru --}}
                        <form method="POST" action="{{ route('guru.destroy', $g->id_guru) }}" style="display:inline;">
                            @csrf
                            @method('DELETE')
                            <button class="btn-delete" onclick="return confirm('Hapus guru ini?')">Hapus</button>
                        </form>
                    </td>
                </tr>
            @endforeach
        </tbody>
    </table>
@endsection
