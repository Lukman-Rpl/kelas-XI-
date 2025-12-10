@extends('layouts.app')

@section('content')
<div class="container">
    <h2>Data Jurusan</h2>

    @if(session('success'))
        <div class="success">{{ session('success') }}</div>
    @endif

    <form method="POST" action="{{ route('jurusan.store') }}">
        @csrf
        <input type="text" name="nama_jurusan" placeholder="Nama Jurusan" required>
        <button type="submit">Tambah</button>
    </form>

    <table border="1" cellpadding="8" style="margin-top: 15px;">
        <tr>
            <th>No</th>
            <th>Nama Jurusan</th>
            <th>Aksi</th>
        </tr>
        @foreach($jurusan as $i => $j)
        <tr>
            <td>{{ $i + 1 }}</td>
            <td>{{ $j->nama_jurusan }}</td>
            <td>
                <a href="{{ route('jurusan.edit', $j->id) }}">Edit</a>
                <form action="{{ route('jurusan.destroy', $j->id) }}" method="POST" style="display:inline;">
                    @csrf
                    @method('DELETE')
                    <button type="submit" onclick="return confirm('Yakin ingin menghapus?')">Hapus</button>
                </form>
            </td>
        </tr>
        @endforeach
    </table>
</div>
@endsection
