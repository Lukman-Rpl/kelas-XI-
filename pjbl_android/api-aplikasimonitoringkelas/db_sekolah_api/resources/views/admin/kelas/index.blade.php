@extends('layouts.app')
@section('content')
<h2>Daftar Kelas</h2>
<a href="{{ route('kelas.create') }}">Tambah Kelas</a>
<table border="1" cellpadding="8">
<tr><th>No</th><th>Kelas</th><th>Jurusan</th><th>Tahun Ajaran</th><th>Aksi</th></tr>
@foreach($kelas as $i=>$k)
<tr>
<td>{{ $i+1 }}</td>
<td>{{ $k->nama_kelas }}</td>
<td>{{ $k->jurusan->nama_jurusan }}</td>
<td>{{ $k->tahunAjaran->tahun }}</td>
<td>
<a href="{{ route('kelas.edit',$k->id) }}">Edit</a>
<form action="{{ route('kelas.destroy',$k->id) }}" method="POST">@csrf @method('DELETE')
<button type="submit">Hapus</button></form>
</td>
</tr>
@endforeach
</table>
@endsection
