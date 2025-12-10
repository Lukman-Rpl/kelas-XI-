@extends('layouts.app')

@section('title', 'Edit Guru')

@section('content')
    <h2>✏️ Edit Guru</h2>

    <form method="POST" action="{{ route('guru.update', $guru->id_guru) }}" class="form-box">
        @csrf
        @method('PUT')

        {{-- Nama Guru --}}
        <input type="text" name="nama_guru" value="{{ $guru->nama_guru }}" required>

        {{-- NIP --}}
        <input type="text" name="nip" value="{{ $guru->nip }}">

        {{-- Mapel --}}
        <select name="mapel_id" required>
            @foreach($mapel as $m)
                <option value="{{ $m->id }}" {{ $guru->mapel_id == $m->id ? 'selected' : '' }}>
                    {{ $m->nama_mapel }}
                </option>
            @endforeach
        </select>

        <button type="submit">Simpan Perubahan</button>
    </form>
@endsection
