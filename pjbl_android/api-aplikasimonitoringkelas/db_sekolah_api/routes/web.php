<?php

use Illuminate\Support\Facades\Route;

// ===== Controllers Admin =====
use App\Http\Controllers\AuthController;
use App\Http\Controllers\DashboardController;
use App\Http\Controllers\GuruController;
use App\Http\Controllers\MapelController;
use App\Http\Controllers\JadwalController;
use App\Http\Controllers\JurusanController;
use App\Http\Controllers\TahunAjaranController;
use App\Http\Controllers\KelasController;
use App\Http\Controllers\IjinController;
use App\Http\Controllers\UserController;
use App\Http\Controllers\GuruPenggantiController;


// ================= AUTH =====================
Route::get('/login', [AuthController::class, 'showLoginForm'])->name('login');
Route::post('/login', [AuthController::class, 'login'])->name('login.process');
Route::get('/logout', [AuthController::class, 'logout'])->name('logout');

Route::get('/', fn() => redirect('/login'));


// ================= ADMIN PROTECTED ROUTES =====================
Route::middleware(['auth', 'admin'])->group(function () {

    // Dashboard
    Route::get('/admin/dashboard', [DashboardController::class, 'index'])->name('admin.dashboard');


    /* ===========================================================
    | CRUD GURU
    ============================================================*/
    // ===================== GURU =====================
Route::get('/admin/guru', [GuruController::class, 'index'])->name('guru.index');
Route::post('/admin/guru', [GuruController::class, 'store'])->name('guru.store');
Route::get('/admin/guru/{id}/edit', [GuruController::class, 'edit'])->name('guru.edit');
Route::put('/admin/guru/{id}', [GuruController::class, 'update'])->name('guru.update');
Route::delete('/admin/guru/{id}', [GuruController::class, 'destroy'])->name('guru.destroy');
Route::post('/admin/guru/import', [GuruController::class, 'importExcel'])->name('guru.import');


    /* ===========================================================
    | CRUD MAPEL
    ============================================================*/
    Route::get('/mapel', [MapelController::class, 'index'])->name('mapel.index');
    Route::post('/mapel', [MapelController::class, 'store'])->name('mapel.store');
    Route::get('/mapel/{id}/edit', [MapelController::class, 'edit'])->name('mapel.edit');
    Route::put('/mapel/{id}', [MapelController::class, 'update'])->name('mapel.update');
    Route::delete('/mapel/{id}', [MapelController::class, 'destroy'])->name('mapel.destroy');
    Route::post('/mapel/import', [MapelController::class, 'importExcel'])->name('mapel.import');


    /* ===========================================================
    | CRUD JADWAL
    ============================================================*/
    Route::get('/jadwal', [JadwalController::class, 'index'])->name('jadwal.index');
    Route::get('/jadwal/create', [JadwalController::class, 'create'])->name('jadwal.create');
    Route::post('/jadwal', [JadwalController::class, 'store'])->name('jadwal.store');
    Route::get('/jadwal/{id}/edit', [JadwalController::class, 'edit'])->name('jadwal.edit');
    Route::put('/jadwal/{id}', [JadwalController::class, 'update'])->name('jadwal.update');
    Route::delete('/jadwal/{id}', [JadwalController::class, 'destroy'])->name('jadwal.destroy');
    Route::post('/jadwal/import', [JadwalController::class, 'import'])->name('jadwal.import');


    /* ===========================================================
    | CRUD USERS
    ============================================================*/
  // ===================== USERS =====================

Route::get('/admin/users', [UserController::class, 'index'])->name('admin.users.index');
Route::get('/admin/users/create', [UserController::class, 'create'])->name('admin.users.create');
Route::post('/admin/users', [UserController::class, 'store'])->name('admin.users.store');
Route::get('/admin/users/{user}/edit', [UserController::class, 'edit'])->name('admin.users.edit');
Route::put('/admin/users/{user}', [UserController::class, 'update'])->name('admin.users.update');
Route::delete('/admin/users/{user}', [UserController::class, 'destroy'])->name('admin.users.destroy');
Route::post('/admin/users/import', [UserController::class, 'import'])->name('users.import');


    /* ===========================================================
    | CRUD KELAS
    ============================================================*/
    Route::get('/kelas', [KelasController::class, 'index'])->name('kelas.index');
    Route::get('/kelas/create', [KelasController::class, 'create'])->name('kelas.create');
    Route::post('/kelas', [KelasController::class, 'store'])->name('kelas.store');
    Route::get('/kelas/{id}/edit', [KelasController::class, 'edit'])->name('kelas.edit');
    Route::put('/kelas/{id}', [KelasController::class, 'update'])->name('kelas.update');
    Route::delete('/kelas/{id}', [KelasController::class, 'destroy'])->name('kelas.destroy');
    Route::post('/kelas/import', [KelasController::class, 'import'])->name('kelas.import');


    /* ===========================================================
    | CRUD JURUSAN
    ============================================================*/
    Route::get('/jurusan', [JurusanController::class, 'index'])->name('jurusan.index');
    Route::get('/jurusan/create', [JurusanController::class, 'create'])->name('jurusan.create');
    Route::post('/jurusan', [JurusanController::class, 'store'])->name('jurusan.store');
    Route::get('/jurusan/{id}/edit', [JurusanController::class, 'edit'])->name('jurusan.edit');
    Route::put('/jurusan/{id}', [JurusanController::class, 'update'])->name('jurusan.update');
    Route::delete('/jurusan/{id}', [JurusanController::class, 'destroy'])->name('jurusan.destroy');
    Route::post('/jurusan/import', [JurusanController::class, 'import'])->name('jurusan.import');


    /* ===========================================================
    | CRUD TAHUN AJARAN
    ============================================================*/
    Route::get('/tahun-ajaran', [TahunAjaranController::class, 'index'])->name('tahun-ajaran.index');
    Route::get('/tahun-ajaran/create', [TahunAjaranController::class, 'create'])->name('tahun-ajaran.create');
    Route::post('/tahun-ajaran', [TahunAjaranController::class, 'store'])->name('tahun-ajaran.store');
    Route::get('/tahun-ajaran/{tahun}/edit', [TahunAjaranController::class, 'edit'])->name('tahun-ajaran.edit');
    Route::put('/tahun-ajaran/{tahun}', [TahunAjaranController::class, 'update'])->name('tahun-ajaran.update');
    Route::delete('/tahun-ajaran/{tahun}', [TahunAjaranController::class, 'destroy'])->name('tahun-ajaran.destroy');    
    Route::post('/tahun-ajaran/import', [TahunAjaranController::class, 'import'])->name('tahun-ajaran.import');


    /* ===========================================================
    | CRUD IJIN
    ============================================================*/
    Route::get('/ijin', [IjinController::class, 'index'])->name('ijin.index');
    Route::get('/ijin/create', [IjinController::class, 'create'])->name('ijin.create');
    Route::post('/ijin', [IjinController::class, 'store'])->name('ijin.store');
    Route::get('/ijin/{id}/edit', [IjinController::class, 'edit'])->name('ijin.edit');
    Route::put('/ijin/{id}', [IjinController::class, 'update'])->name('ijin.update');
    Route::delete('/ijin/{id}', [IjinController::class, 'destroy'])->name('ijin.destroy');

// CRUD Guru Pengganti
Route::get('/guru_pengganti', [GuruPenggantiController::class, 'index'])->name('guru_pengganti.index');
Route::get('/guru_pengganti/create', [GuruPenggantiController::class, 'create'])->name('guru_pengganti.create');
Route::post('/guru_pengganti', [GuruPenggantiController::class, 'store'])->name('guru_pengganti.store');
Route::get('/guru_pengganti/{id}/edit', [GuruPenggantiController::class, 'edit'])->name('guru_pengganti.edit');
Route::put('/guru_pengganti/{id}', [GuruPenggantiController::class, 'update'])->name('guru_pengganti.update');
Route::delete('/guru_pengganti/{id}', [GuruPenggantiController::class, 'destroy'])->name('guru_pengganti.destroy');
Route::post('/guru_pengganti/import', [GuruPenggantiController::class, 'importExcel'])->name('guru_pengganti.import');


});
