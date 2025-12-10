@extends('layouts.app')

@section('content')

<div class="container mt-4">

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h3 class="fw-bold">Daftar Ijin Guru</h3>
        <a href="{{ route('ijin.create') }}" class="btn btn-primary">
            + Tambah Ijin
        </a>
    </div>

    @if(session('success'))
        <div class="alert alert-success">
            {{ session('success') }}
        </div>
    @endif

    <div class="card shadow-sm">
        <div class="card-body">

            <table class="table table-bordered table-hover">
                <thead class="table-dark">
                    <tr>
                        <th>No</th>
                        <th>Nama Guru</th>
                        <th>Tanggal Mulai</th>
                        <th>Tanggal Selesai</th>
                        <th>Hari</th>  <!-- Kolom baru -->
                        <th>Status</th>
                        <th>Keterangan</th>
                        <th>Aksi</th>
                    </tr>
                </thead>

                <tbody>
                    @forelse ($data as $key => $row)
                        <tr>
                            <td>{{ $key + 1 }}</td>
                            <td>{{ $row->guru->nama ?? $row->nama_guru }}</td>

                            <td>{{ $row->tanggal_mulai }}</td>
                            <td>{{ $row->tanggal_selesai }}</td>

                            <td><strong>{{ $row->hari }}</strong> Hari</td> <!-- Kolom hari -->

                            <td>
                                <span class="badge 
                                    @if($row->status == 'sakit') bg-danger 
                                    @elseif($row->status == 'ijin') bg-warning 
                                    @endif
                                ">
                                    {{ ucfirst($row->status) }}
                                </span>
                            </td>

                            <td>{{ $row->keterangan ?? '-' }}</td>

                            <td>
                                <a href="{{ route('admin.ijin.edit', $row->id) }}" 
                                   class="btn btn-sm btn-warning">
                                    Edit
                                </a>

                                <form action="{{ route('admin.ijin.destroy', $row->id) }}" 
                                      method="POST" 
                                      class="d-inline"
                                      onsubmit="return confirm('Yakin ingin menghapus data ini?')">
                                    @csrf
                                    @method('DELETE')
                                    <button class="btn btn-sm btn-danger">
                                        Hapus
                                    </button>
                                </form>
                            </td>

                        </tr>
                    @empty
                        <tr>
                            <td colspan="8" class="text-center text-muted">
                                Belum ada data ijin.
                            </td>
                        </tr>
                    @endforelse
                </tbody>

            </table>

        </div>
    </div>

</div>

@endsection
