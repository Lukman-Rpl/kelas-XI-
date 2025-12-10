@extends('layouts.app')

@section('title', 'Edit Mapel')

@section('content')
    <h2>✏️ Edit Mata Pelajaran</h2>

    <form method="POST" action="{{ route('mapel.update', $mapel->id) }}" class="form-box">
        @csrf
        @method('PUT')

        <input type="text" name="nama_mapel" value="{{ $mapel->nama_mapel }}" required>

        <button type="submit">Simpan Perubahan</button>
    </form>
@endsection
