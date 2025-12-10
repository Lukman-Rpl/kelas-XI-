@extends('layouts.app')

@section('title', 'Edit Jurusan')

@section('content')
<div class="container">
    <h2>✏️ Edit Jurusan</h2>

    <div class="form-box">
        <form method="POST" action="{{ route('jurusan.update', $jurusan->id) }}">
            @csrf
            @method('PUT')

            <input type="text" name="nama_jurusan" value="{{ $jurusan->nama_jurusan }}" required>

            <button type="submit">💾 Simpan Perubahan</button>
        </form>
    </div>
</div>
@endsection
