@extends('layouts.app')

@section('content')
<div class="container">
    <h2>Data Tahun Ajaran</h2>

    @if(session('success'))
        <div class="success">{{ session('success') }}</div>
    @endif

    <form method="POST" action="{{ route('tahun-ajaran.store') }}">
        @csrf
        <input type="text" name="tahun_ajaran" placeholder="Tahun Ajaran (contoh: 2025/2026)" required>
        <button type="submit">Tambah</button>
    </form>

    <table border="1" cellpadding="8" style="margin-top: 15px;">
        <tr>
            <th>No</th>
            <th>Tahun Ajaran</th>
            <th>Aksi</th>
        </tr>
        @foreach($tahunAjaran as $i => $t)
        <tr>
            <td>{{ $i + 1 }}</td>
            <td>{{ $t->tahun_ajaran }}</td>
            <td>
                <a href="{{ route('tahun-ajaran.edit', $t->id) }}">Edit</a>
                <form action="{{ route('tahun-ajaran.destroy', $t->id) }}" method="POST" style="display:inline;">
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
