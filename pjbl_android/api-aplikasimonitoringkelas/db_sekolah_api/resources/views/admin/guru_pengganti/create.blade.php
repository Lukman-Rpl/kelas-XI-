@extends('layouts.app')

@section('title', 'Tambah Guru Pengganti')

@section('content')
<div class="container">
    <h1>Tambah Guru Pengganti</h1>

    @if ($errors->any())
        <div class="alert alert-danger">
            <ul>
                @foreach ($errors->all() as $error)
                    <li>{{ $error }}</li>
                @endforeach
            </ul>
        </div>
    @endif

    <form action="{{ route('guru_pengganti.store') }}" method="POST">
        @csrf
        <div class="mb-3">
            <label>Guru</label>
            <select name="guru_id" class="form-control" required>
                <option value="">-- Pilih Guru --</option>
                @foreach($guru as $g)
                    <option value="{{ $g->id_guru }}">{{ $g->nama_guru }}</option>
                @endforeach
            </select>
        </div>

        <div class="mb-3">
            <label>Guru Pengganti</label>
            <select name="guru_pengganti_id" class="form-control" required>
                <option value="">-- Pilih Pengganti --</option>
                @foreach($guru as $g)
                    <option value="{{ $g->id_guru }}">{{ $g->nama_guru }}</option>
                @endforeach
            </select>
        </div>

        <div class="mb-3">
            <label>Mata Pelajaran</label>
            <select name="mapel_id" class="form-control" required>
                <option value="">-- Pilih Mapel --</option>
                @foreach($mapel as $m)
                    <option value="{{ $m->id }}">{{ $m->nama_mapel }}</option>
                @endforeach
            </select>
        </div>

        <div class="mb-3">
            <label>Kelas</label>
            <select name="kelas_id" class="form-control" required>
                <option value="">-- Pilih Kelas --</option>
                @foreach($kelas as $k)
                    <option value="{{ $k->id }}">{{ $k->nama_kelas }}</option>
                @endforeach
            </select>
        </div>

        <div class="mb-3">
            <label>Tanggal</label>
            <input type="date" name="tanggal" class="form-control" required>
        </div>

        <div class="mb-3">
            <label>Jam</label>
            <input type="text" name="jam" class="form-control" placeholder="08:00 - 09:00" required>
        </div>

        <div class="mb-3">
            <label>Keterangan</label>
            <textarea name="keterangan" class="form-control" rows="2"></textarea>
        </div>

        <button type="submit" class="btn btn-primary">Simpan</button>
        <a href="{{ route('guru_pengganti.index') }}" class="btn btn-secondary">Kembali</a>
    </form>
</div>
@endsection
