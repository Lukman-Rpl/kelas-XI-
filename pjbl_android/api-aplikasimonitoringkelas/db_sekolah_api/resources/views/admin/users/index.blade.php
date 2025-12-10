<!DOCTYPE html>
<html>
<head>
    <title>Manajemen User</title>
</head>
<body>

<h2>Manajemen Users</h2>

@if (session('success'))
    <div style="color: green;">{{ session('success') }}</div>
@endif

{{-- FORM TAMBAH USER --}}
<form method="POST" action="{{ route('admin.users.store') }}">
    @csrf

    {{-- PERBAIKAN: name="name" --}}
    <input type="text" name="nama" placeholder="Nama" required>

    <input type="email" name="email" placeholder="Email" required>

    <input type="password" name="password" placeholder="Password" required>

    <select name="role" required>
        <option value="admin">Admin</option>
        <option value="kurikulum">Kurikulum</option>
        <option value="siswa">Siswa</option>
        <option value="kepala-sekolah">Kepala Sekolah</option>
    </select>

    {{-- Dropdown Kelas --}}
    <select name="kelas_id">
        <option value="">Tidak Ada</option>
        @foreach ($kelas as $k)
            <option value="{{ $k->id }}">{{ $k->nama_kelas }}</option>
        @endforeach
    </select>

    <button type="submit">Tambah</button>
</form>


<h3>Daftar User</h3>

<table border="1" cellpadding="8">
    <tr>
        <th>No</th>
        <th>Nama</th>
        <th>Email</th>
        <th>Role</th>
        <th>Kelas</th>
        <th>Aksi</th>
    </tr>

    @foreach ($users as $i => $user)
    <tr>
        <td>{{ $i+1 }}</td>

        {{-- PERBAIKAN: gunakan $user->nama --}}
        <td>{{ $user->nama }}</td>

        <td>{{ $user->email }}</td>
        <td>{{ $user->role }}</td>
        <td>{{ $user->kelas->nama_kelas ?? '-' }}</td>

        <td>
            <a href="{{ route('admin.users.edit', $user->id) }}">Edit</a>

            <form method="POST" action="{{ route('admin.users.destroy', $user->id) }}" style="display:inline;">
                @csrf 
                @method('DELETE')
                <button onclick="return confirm('Hapus user?')">Hapus</button>
            </form>
        </td>
    </tr>
    @endforeach
</table>

</body>
</html>