<x-layouts.auth.simple>
    <div class="w-full max-w-md rounded-2xl border border-blue-100 bg-white p-8 shadow-xl shadow-blue-100/60">
        <div class="mb-7 text-center"><p class="text-sm font-semibold uppercase tracking-[0.2em] text-blue-600">SakuBijak</p><h1 class="mt-3 text-2xl font-bold text-blue-950">Buat akun baru</h1><p class="mt-2 text-sm text-slate-500">Mulai kelola keuangan Anda hari ini.</p></div>
        <form action="{{ route('register') }}" method="POST" class="space-y-4">
            @csrf
            <div><label for="name" class="block text-sm font-medium text-slate-700">Nama lengkap</label><input type="text" id="name" name="name" value="{{ old('name') }}" required autofocus class="mt-1 block w-full rounded-lg border border-blue-200 px-4 py-2.5 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100">@error('name')<p class="mt-1 text-xs text-red-600">{{ $message }}</p>@enderror</div>
            <div><label for="email" class="block text-sm font-medium text-slate-700">Alamat email</label><input type="email" id="email" name="email" value="{{ old('email') }}" required class="mt-1 block w-full rounded-lg border border-blue-200 px-4 py-2.5 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100">@error('email')<p class="mt-1 text-xs text-red-600">{{ $message }}</p>@enderror</div>
            <div><label for="password" class="block text-sm font-medium text-slate-700">Kata sandi</label><input type="password" id="password" name="password" required class="mt-1 block w-full rounded-lg border border-blue-200 px-4 py-2.5 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100">@error('password')<p class="mt-1 text-xs text-red-600">{{ $message }}</p>@enderror</div>
            <div><label for="password_confirmation" class="block text-sm font-medium text-slate-700">Konfirmasi kata sandi</label><input type="password" id="password_confirmation" name="password_confirmation" required class="mt-1 block w-full rounded-lg border border-blue-200 px-4 py-2.5 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"></div>
            <button type="submit" class="mt-2 w-full rounded-lg bg-blue-600 py-2.5 font-semibold text-white transition hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-300">Daftar</button>
        </form>
        <p class="mt-6 text-center text-sm text-slate-600">Sudah punya akun? <a href="{{ route('login') }}" class="font-semibold text-blue-600 hover:underline">Masuk di sini</a></p>
    </div>
</x-layouts.auth.simple>
