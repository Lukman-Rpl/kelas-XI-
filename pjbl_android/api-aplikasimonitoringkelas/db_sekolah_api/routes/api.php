<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\JadwalController;
use App\Http\Controllers\Api\GuruController;
use App\Http\Controllers\Api\IjinApiController;
use App\Http\Controllers\Api\MapelApiController;
use App\Http\Controllers\Api\KelasController;
use App\Http\Controllers\Api\GuruPenggantiApiController;

// ======================== AUTH ========================
Route::post('/register', [AuthController::class, 'register']);
Route::post('/login', [AuthController::class, 'login']);

// ======================== USERS =======================
Route::get('/users', [AuthController::class, 'index']);
Route::get('/users/{id}', [AuthController::class, 'show']);
Route::put('/users/{id}', [AuthController::class, 'update']);
Route::delete('/users/{id}', [AuthController::class, 'destroy']);

// ======================== JADWAL =======================
// Jadwal dengan filter:
// /jadwal?kelas_id=1&hari=senin&tanggal=2025-11-16&status=hadir
Route::get('/jadwal', [JadwalController::class, 'index']);

// Tambah jadwal
Route::post('/jadwal', [JadwalController::class, 'store']);

// Detail jadwal berdasarkan ID
Route::get('/jadwal/{id}', [JadwalController::class, 'show']);

// Update jadwal
Route::put('/jadwal/{id}', [JadwalController::class, 'update']);

// Hapus jadwal
Route::delete('/jadwal/{id}', [JadwalController::class, 'destroy']);

// ======================== GURU =======================
Route::get('/guru', [GuruController::class, 'index']);
Route::get('/guru/{id}', [GuruController::class, 'show']);
Route::get('/guru/mapel/{id_mapel}', [GuruController::class, 'getGuruByMapel']);

Route::post('/guru', [GuruController::class, 'store']);
Route::put('/guru/{id}', [GuruController::class, 'update']);
Route::delete('/guru/{id}', [GuruController::class, 'destroy']);


Route::get('/ijin', [IjinApiController::class, 'index']);           // List semua ijin
Route::post('/ijin', [IjinApiController::class, 'store']);          // Tambah ijin
Route::get('/ijin/{id}', [IjinApiController::class, 'show']);       // Detail ijin
Route::put('/ijin/{id}', [IjinApiController::class, 'update']);     // Update ijin
Route::delete('/ijin/{id}', [IjinApiController::class, 'destroy']); // Hapus ijin

Route::get('/mapel', [MapelApiController::class, 'index']);
Route::post('/mapel', [MapelApiController::class, 'store']);
Route::get('/mapel/{id}', [MapelApiController::class, 'show']);
Route::put('/mapel/{id}', [MapelApiController::class, 'update']);
Route::delete('/mapel/{id}', [MapelApiController::class, 'destroy']);

/*
|--------------------------------------------------------------------------
| KELAS
|--------------------------------------------------------------------------
*/
Route::get('/kelas',               [KelasController::class, 'index']);
Route::post('/kelas',              [KelasController::class, 'store']);
Route::get('/kelas/{id}',          [KelasController::class, 'show']);
Route::put('/kelas/{id}',          [KelasController::class, 'update']);
Route::delete('/kelas/{id}',       [KelasController::class, 'destroy']);

Route::get('/guru-pengganti', [GuruPenggantiApiController::class, 'index']);
Route::get('/guru-pengganti/{id}', [GuruPenggantiApiController::class, 'show']);
Route::post('/guru-pengganti', [GuruPenggantiApiController::class, 'store']);
Route::put('/guru-pengganti/{id}', [GuruPenggantiApiController::class, 'update']);
Route::delete('/guru-pengganti/{id}', [GuruPenggantiApiController::class, 'destroy']);
