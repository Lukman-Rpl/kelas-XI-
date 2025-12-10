<!DOCTYPE html>
<html>
<head>
    <title>Dashboard Admin</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f5f6fa;
            margin: 0;
            padding: 0;
        }
        .navbar {
            background: #2c3e50;
            padding: 12px;
            color: white;
            display: flex;
            justify-content: space-between;
        }
        .navbar a {
            color: white;
            text-decoration: none;
            margin-right: 15px;
        }
        .container {
            padding: 25px;
        }
        .card {
            background: white;
            border-radius: 8px;
            padding: 20px;
            box-shadow: 0 3px 8px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }
        h2 {
            color: #34495e;
        }
        input[type=file], button {
            margin-top: 10px;
        }
        .success {
            background: #d1f7d1;
            color: #2e7d32;
            padding: 10px;
            border-radius: 5px;
        }
        .error {
            background: #f8d7da;
            color: #721c24;
            padding: 10px;
            border-radius: 5px;
        }
        ul { list-style: none; padding: 0; }
        ul li { margin-bottom: 6px; }
    </style>
</head>
<body>

<div class="navbar">
    <div>
        <a href="{{ route('admin.dashboard') }}">Dashboard</a>
        <a href="{{ route('kelas.index') }}">Kelas</a>
        <a href="{{ route('jurusan.index') }}">Jurusan</a>
        <a href="{{ route('tahun-ajaran.index') }}">Tahun Ajaran</a>
        <a href="{{ route('admin.users.index') }}">Users</a>
        <a href="{{ route('guru.index') }}">Guru</a>
        <a href="{{ route('mapel.index') }}">Mapel</a>
        <a href="{{ route('jadwal.index') }}">Jadwal</a>
        <a href="{{ route('ijin.index') }}">Ijin</a>
        <a href="{{ route('guru_pengganti.index') }}">Guru Pengganti</a> <!-- Menu Baru -->
    </div>
    <div>
        <a href="{{ route('logout') }}">Logout</a>
    </div>
</div>

<div class="container">
    <h2>📊 Dashboard Admin</h2>
    <p>Selamat datang, <strong>{{ Auth::user()->name }}</strong> (Role: Admin)</p>

    @if(session('success'))
        <div class="success">{{ session('success') }}</div>
    @endif

    <div class="card">
        <h3>📘 Informasi Cepat</h3>
        <ul>
            <li>Total Kelas: {{ \App\Models\Kelas::count() }}</li>
            <li>Total Jurusan: {{ \App\Models\Jurusan::count() }}</li>
            <li>Total Tahun Ajaran: {{ \App\Models\TahunAjaran::count() }}</li>
            <li>Total Users: {{ \App\Models\User::count() }}</li>
            <li>Total Guru: {{ \App\Models\Guru::count() }}</li>
            <li>Total Mapel: {{ \App\Models\Mapel::count() }}</li>
            <li>Total Jadwal: {{ \App\Models\Jadwal::count() }}</li>
            <li>Total Ijin: {{ \App\Models\Ijin::count() }}</li>
            <li>Total Guru Pengganti: {{ \App\Models\GuruPengganti::count() }}</li> <!-- Info Baru -->
        </ul>
    </div>

    <div class="card">
        <h3>📥 Import Data dari Excel</h3>

        <form action="{{ route('kelas.import') }}" method="POST" enctype="multipart/form-data">
            @csrf
            <label>Import Kelas:</label>
            <input type="file" name="file" accept=".xls,.xlsx" required>
            <button type="submit">Upload</button>
        </form>

        <form action="{{ route('jurusan.import') }}" method="POST" enctype="multipart/form-data">
            @csrf
            <label>Import Jurusan:</label>
            <input type="file" name="file" accept=".xls,.xlsx" required>
            <button type="submit">Upload</button>
        </form>

        <form action="{{ route('tahun-ajaran.import') }}" method="POST" enctype="multipart/form-data">
            @csrf
            <label>Import Tahun Ajaran:</label>
            <input type="file" name="file" accept=".xls,.xlsx" required>
            <button type="submit">Upload</button>
        </form>

        <form action="{{ route('users.import') }}" method="POST" enctype="multipart/form-data">
            @csrf
            <label>Import Users:</label>
            <input type="file" name="file" accept=".xls,.xlsx" required>
            <button type="submit">Upload</button>
        </form>

        <form action="{{ route('guru.import') }}" method="POST" enctype="multipart/form-data">
            @csrf
            <label>Import Guru:</label>
            <input type="file" name="file" accept=".xls,.xlsx" required>
            <button type="submit">Upload</button>
        </form>

        <form action="{{ route('mapel.import') }}" method="POST" enctype="multipart/form-data">
            @csrf
            <label>Import Mapel:</label>
            <input type="file" name="file" accept=".xls,.xlsx" required>
            <button type="submit">Upload</button>
        </form>

        <form action="{{ route('jadwal.import') }}" method="POST" enctype="multipart/form-data">
            @csrf
            <label>Import Jadwal:</label>
            <input type="file" name="file" accept=".xls,.xlsx" required>
            <button type="submit">Upload</button>
        </form>

        <form action="{{ route('guru_pengganti.import') }}" method="POST" enctype="multipart/form-data"> <!-- Form Baru -->
            @csrf
            <label>Import Guru Pengganti:</label>
            <input type="file" name="file" accept=".xls,.xlsx" required>
            <button type="submit">Upload</button>
        </form>
    </div>
</div>

</body>
</html>
