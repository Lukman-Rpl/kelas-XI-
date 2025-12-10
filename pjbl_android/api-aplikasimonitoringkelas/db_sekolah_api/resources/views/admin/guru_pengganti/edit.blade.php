@extends('layouts.app')

@section('title', 'Edit Guru Pengganti')

@section('content')
<div class="container">
    <h1>Edit Guru Pengganti</h1>

    @if ($errors->any())
        <div class="alert alert-danger">
            <ul>
                @foreach ($errors->all() as $error)
                    <li>{{ $error }}</li>
                @endforeach
            </ul>
        </div>
    @endif

    <form action="{{ route('guru_pengganti.update', $data->id) }}" method="POST">
        @csrf
        @method('PUT')

        <div class="mb-3">
            <label>Guru</label>
            <select name="guru_id" class="form-control" required>
                @foreach($guru as $g)
                    <option value="{{ $g->id_guru }}" {{ $data->guru_id == $g->id_guru ? 'selected' : '' }}>
                        {{ $g->nama_guru }}
                    </option>
                @endforeach
            </select>
        </div>

        <div class="mb-3">
            <label>Guru Pengganti</label>
            <select name="guru_pengganti_id" class="form-control" required>
                @foreach($guru as $g)
                    <option value="{{ $g->id_guru }}" {{ $data->guru_pengganti_id == $g->id_guru ? 'selected' : '' }}>
                        {{ $g->nama_guru }}
                    </option>
                @endforeach
            </select>
        </div>

        <div class="mb-3">
            <label>Mata Pelajaran</label>
            <select name="mapel_id" class="form-control" required>
                @foreach($mapel as $m)
                    <option value="{{ $m->id }}" {{ $data->mapel_id == $m->id ? 'selected' : '' }}>
                        {{ $m->nama_mapel }}
                    </option>
                @endforeach
            </select>
        </div>

        <div class="mb-3">
            <label>Kelas</label>
            <select name="kelas_id" class="form-control" required>
                @foreach($kelas as $k)
                    <option value="{{ $k->id }}" {{ $data->kelas_id == $k->id ? 'selected' : '' }}>
                        {{ $k->nama_kelas }}
                    </option>
                @endforeach
            </select>
        </div>

        <div class="mb-3">
            <label>Tanggal</label>
            <input type="date" name="tanggal" class="form-control" value="{{ $data->tanggal }}" required>
        </div>

        <div class="mb-3">
            <label>Jam</label>
            <input type="text" name="jam" class="form-control" value="{{ $data->jam }}" required>
        </div>

        <div class="mb-3">
            <label>Keterangan</label>
            <textarea name="keterangan" class="form-control" rows="2">{{ $data->keterangan }}</textarea>
        </div>

        <button type="submit" class="btn btn-primary">Update</button>
        <a href="{{ route('guru_pengganti.index') }}" class="btn btn-secondary">Kembali</a>
    </form>
</div>
@endsection
