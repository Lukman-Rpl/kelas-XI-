@extends('layouts.app')

@section('content')
<div class="container">
    <h2>Edit Tahun Ajaran</h2>

    @if(session('success'))
        <div class="success">{{ session('success') }}</div>
    @endif

    <form method="POST" action="{{ route('tahun-ajaran.update', $tahun->id) }}">
        @csrf
        @method('PUT')

        <input 
            type="text" 
            name="tahun" 
            value="{{ $tahun->tahun }}" 
            placeholder="Tahun Ajaran"
            required
        >

        <button type="submit">Simpan Perubahan</button>
    </form>
</div>
@endsection
