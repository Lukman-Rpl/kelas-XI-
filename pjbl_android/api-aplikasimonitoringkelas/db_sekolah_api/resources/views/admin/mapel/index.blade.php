@extends('layouts.app')

@section('title', 'Data Mapel')

@section('content')
    <h2>📘 Data Mata Pelajaran</h2>

    {{-- Form Tambah Mapel --}}
    <form method="POST" action="{{ route('mapel.store') }}" class="form-box">
        @csrf
        <input type="text" name="nama_mapel" placeholder="Nama Mapel" required>
        <button type="submit">Tambah</button>
    </form>

    @if (session('success'))
        <div class="alert success">{{ session('success') }}</div>
    @endif

    {{-- Tabel Mapel --}}
    <table class="table">
        <thead>
            <tr>
                <th>No</th>
                <th>Nama Mapel</th>
                <th>Aksi</th>
            </tr>
        </thead>

        <tbody>
            @foreach($mapel as $i => $m)
                <tr>
                    <td>{{ $i + 1 }}</td>
                    <td>{{ $m->nama_mapel }}</td>
                    <td>
                        <a href="{{ route('mapel.edit', $m->id) }}" class="btn-edit">Edit</a>

                        <form method="POST" action="{{ route('mapel.destroy', $m->id) }}" style="display:inline;">                        
                            @csrf
                            @method('DELETE')
                            <button class="btn-delete" onclick="return confirm('Hapus mapel ini?')">Hapus</button>
                        </form>
                    </td>
                </tr>
            @endforeach
        </tbody>
    </table>
@endsection
