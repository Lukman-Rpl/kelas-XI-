@extends('layouts.app')

@section('title', 'Data Jadwal')

@section('content')
    <h2>📅 Data Jadwal Pelajaran</h2>

    {{-- Form Tambah Jadwal --}}
    <form method="POST" action="{{ route('jadwal.store') }}" class="form-box">
        @csrf
        
        <select name="hari" required>
            <option value="">-- Pilih Hari --</option>
            @foreach(['Senin','Selasa','Rabu','Kamis','Jumat'] as $hari)
                <option>{{ $hari }}</option>
            @endforeach
        </select>

        <input type="number" name="jam_ke" placeholder="Jam Ke" required>
        <input type="number" name="sampai_jam" placeholder="Sampai Jam" required>

        <select name="mapel_id" required>
            <option value="">-- Pilih Mapel --</option>
            @foreach($mapel as $m)
                <option value="{{ $m->id }}">{{ $m->nama_mapel }}</option>
            @endforeach
        </select>

        <select name="guru_id" required>
            <option value="">-- Pilih Guru --</option>
            @foreach($guru as $g)
                <option value="{{ $g->id }}">{{ $g->nama_guru }}</option>
            @endforeach
        </select>

        <select name="kelas" required>
            <option value="">-- Pilih Kelas --</option>
            @foreach(['X RPL','XI RPL','XII RPL'] as $k)
                <option>{{ $k }}</option>
            @endforeach
        </select>

        <select name="status" required>
            <option value="Hadir">Hadir</option>
            <option value="Tidak Hadir">Tidak Hadir</option>
        </select>

        <input type="text" name="keterangan" placeholder="Keterangan (opsional)">

        <button type="submit">Tambah Jadwal</button>
    </form>

    @if (session('success'))
        <div class="alert success">{{ session('success') }}</div>
    @endif

    {{-- Tabel Jadwal --}}
    <table class="table">
        <thead>
            <tr>
                <th>No</th>
                <th>Hari</th>
                <th>Jam</th>
                <th>Mapel</th>
                <th>Guru</th>
                <th>Kelas</th>
                <th>Status</th>
                <th>Keterangan</th>
                <th>Aksi</th>
            </tr>
        </thead>

        <tbody>
            @foreach($jadwal as $i => $j)
                <tr>
                    <td>{{ $i+1 }}</td>
                    <td>{{ $j->hari }}</td>

                    {{-- Jam tampil sebagai "1 - 2" --}}
                    <td>{{ $j->jam_ke }} - {{ $j->sampai_jam }}</td>

                    <td>{{ $j->mapel->nama_mapel ?? '-' }}</td>
                    <td>{{ $j->guru->nama_guru ?? '-' }}</td>

                    <td>{{ $j->kelas }}</td>
                    <td>{{ $j->status }}</td>
                    <td>{{ $j->keterangan }}</td>

                    <td>
                        <a href="{{ route('jadwal.edit', $j->id) }}" class="btn-edit">Edit</a>

                        <form method="POST" action="{{ route('jadwal.destroy', $j->id) }}" style="display:inline;">
                            @csrf
                            @method('DELETE')
                            <button class="btn-delete" onclick="return confirm('Hapus jadwal ini?')">
                                Hapus
                            </button>
                        </form>
                    </td>
                </tr>
            @endforeach
        </tbody>
    </table>
@endsection
