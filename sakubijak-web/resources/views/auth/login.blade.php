<x-layouts.auth.simple>
    <div class="w-full max-w-md rounded-2xl border border-blue-100 bg-white p-8 shadow-xl shadow-blue-100/60">
        <div class="mb-7 text-center">
            <p class="text-sm font-semibold uppercase tracking-[0.2em] text-blue-600">SakuBijak</p>
            <h1 class="mt-3 text-2xl font-bold text-blue-950">Selamat datang kembali</h1>
            <p class="mt-2 text-sm text-slate-500">Masuk untuk mengelola keuangan Anda.</p>
        </div>
        @if (session('status'))<div class="mb-4 rounded-lg bg-blue-50 p-3 text-sm text-blue-700">{{ session('status') }}</div>@endif
        <form action="{{ route('login') }}" method="POST" class="space-y-4">
            @csrf
            <div><label for="email" class="block text-sm font-medium text-slate-700">Alamat email</label><input type="email" id="email" name="email" value="{{ old('email') }}" required autofocus class="mt-1 block w-full rounded-lg border border-blue-200 px-4 py-2.5 text-sm outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100">@error('email')<p class="mt-1 text-xs text-red-600">{{ $message }}</p>@enderror</div>
            <div><label for="password" class="block text-sm font-medium text-slate-700">Kata sandi</label><input type="password" id="password" name="password" required class="mt-1 block w-full rounded-lg border border-blue-200 px-4 py-2.5 text-sm outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100">@error('password')<p class="mt-1 text-xs text-red-600">{{ $message }}</p>@enderror</div>
            <div class="flex items-center justify-between text-sm"><label class="flex items-center gap-2 text-slate-600"><input type="checkbox" name="remember" class="rounded border-blue-300 text-blue-600 focus:ring-blue-500"> Ingat saya</label>@if (Route::has('password.request'))<a href="{{ route('password.request') }}" class="font-medium text-blue-600 hover:text-blue-700 hover:underline">Lupa password?</a>@endif</div>
            <button type="submit" class="w-full rounded-lg bg-blue-600 py-2.5 font-semibold text-white transition hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-300">Masuk</button>
        </form>
        <div class="my-6 flex items-center gap-3 text-xs text-slate-400"><span class="h-px flex-1 bg-blue-100"></span><span>ATAU</span><span class="h-px flex-1 bg-blue-100"></span></div>
        <a href="{{ route('google.redirect') }}" aria-label="Masuk dengan Google" class="flex w-full items-center justify-center gap-2 rounded-lg border border-blue-200 bg-white py-2.5 text-sm font-medium text-slate-700 transition hover:bg-blue-50"><span class="flex size-5 items-center justify-center rounded-full bg-blue-600 text-xs font-bold text-white">G</span>Masuk dengan Google</a>
        <p class="mt-6 text-center text-sm text-slate-600">Belum punya akun? <a href="{{ route('register') }}" class="font-semibold text-blue-600 hover:underline">Daftar sekarang</a></p>
    </div>
</x-layouts.auth.simple>
