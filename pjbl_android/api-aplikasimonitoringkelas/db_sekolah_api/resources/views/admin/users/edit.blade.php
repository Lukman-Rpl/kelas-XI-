<!DOCTYPE html>
<html>
<head>
    <title>Edit User</title>
    <link rel="stylesheet" href="{{ asset('css/style.css') }}">
</head>
<body>

<div class="navbar">
    <a href="{{ route('admin.dashboard') }}">Dashboard</a>
    <a href="{{ route('admin.users.index') }}">Users</a>
</div>

<h2>Edit User</h2>

<form method="POST" action="{{ route('admin.users.update', $user->id) }}">
    @csrf
    @method('PUT')

    <input type="text" name="nama" value="{{ $user->nama }}" required>

    <input type="email" name="email" value="{{ $user->email }}" required>

    <input type="password" name="password" placeholder="Kosongkan jika tidak diganti">

    <!-- Role sesuai tabel users -->
    <select name="role" required>
        <option value="admin" {{ $user->role=='admin'?'selected':'' }}>Admin</option>
        <option value="kurikulum" {{ $user->role=='kurikulum'?'selected':'' }}>Kurikulum</option>
        <option value="siswa" {{ $user->role=='siswa'?'selected':'' }}>Siswa</option>
        <option value="kepala-sekolah" {{ $user->role=='kepala-sekolah'?'selected':'' }}>Kepala Sekolah</option>
    </select>

    <!-- Sesuai struktur tabel: kelas_id -->
    <select name="kelas_id">
        <option value="" {{ $user->kelas_id == null ? 'selected' : '' }}>Tidak Ada</option>
        <option value="10" {{ $user->kelas_id == 10 ? 'selected' : '' }}>10</option>
        <option value="11" {{ $user->kelas_id == 11 ? 'selected' : '' }}>11</option>
        <option value="12" {{ $user->kelas_id == 12 ? 'selected' : '' }}>12</option>
    </select>

    <button type="submit">Simpan</button>
</form>

</body>
</html>
