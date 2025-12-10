@extends('layouts.app')

@section('title', 'Edit Jadwal')

@section('content')
    <h2>✏️ Edit Jadwal</h2>

    <form method="POST" action="{{ route('jadwal.update', $jadwal->id) }}" class="form-box">
        @csrf
        @method('PUT')

        {{-- HARI --}}
        <select name="hari" required>
            @foreach(['Senin','Selasa','Rabu','Kamis','Jumat'] as $hari)
                <option value="{{ $hari }}" {{ $jadwal->hari == $hari ? 'selected' : '' }}>
                    {{ $hari }}
                </option>
            @endforeach
        </select>

        {{-- JAM --}}
        <input type="number" name="jam_ke" value="{{ $jadwal->jam_ke }}" placeholder="Jam Ke" required>
        <input type="number" name="sampai_jam" value="{{ $jadwal->sampai_jam }}" placeholder="Sampai Jam" required>

        {{-- MAPEL --}}
        <select name="mapel_id" required>
            @foreach($mapel as $m)
                <option value="{{ $m->id }}" 
                    {{ $jadwal->mapel_id == $m->id ? 'selected' : '' }}>
                    {{ $m->nama_mapel }}
                </option>
            @endforeach
        </select>

        {{-- GURU --}}
        <select name="guru_id" required>
            @foreach($guru as $g)
                <option value="{{ $g->id }}" 
                    {{ $jadwal->guru_id == $g->id ? 'selected' : '' }}>
                    {{ $g->nama_guru }}
                </option>
            @endforeach
        </select>

        {{-- KELAS --}}
        <select name="kelas" required>
            @foreach(['X RPL','XI RPL','XII RPL'] as $k)
                <option value="{{ $k }}" {{ $jadwal->kelas == $k ? 'selected' : '' }}>
                    {{ $k }}
                </option>
            @endforeach
        </select>

        {{-- STATUS --}}
        <select name="status" required>
            <option value="Hadir" {{ $jadwal->status == "Hadir" ? 'selected' : '' }}>Hadir</option>
            <option value="Tidak Hadir" {{ $jadwal->status == "Tidak Hadir" ? 'selected' : '' }}>Tidak Hadir</option>
        </select>

        {{-- KETERANGAN --}}
        <input type="text" name="keterangan" value="{{ $jadwal->keterangan }}" placeholder="Keterangan">

        <button type="submit">Simpan Perubahan</button>
    </form>
@endsection
