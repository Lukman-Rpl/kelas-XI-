<!DOCTYPE html>
<html lang="{{ str_replace('_', '-', app()->getLocale()) }}">
    <head>
        @include('partials.head')
    </head>
    <body class="min-h-screen bg-blue-50 text-slate-800">
        <div class="min-h-screen lg:flex">
            <aside class="border-b border-blue-100 bg-white shadow-sm lg:flex lg:min-h-screen lg:w-64 lg:flex-col lg:border-b-0 lg:border-r">
                <div class="flex items-center justify-between border-b border-blue-100 px-6 py-5">
                    <a href="{{ route('dashboard') }}" class="flex items-center gap-3">
                        <x-app-logo class="size-8" href="#"></x-app-logo>
                        <span class="font-bold text-blue-950">SakuBijak</span>
                    </a>
                </div>
                <nav class="flex gap-2 overflow-x-auto px-4 py-4 lg:block lg:flex-1 lg:space-y-2" aria-label="Navigasi utama">
                    @foreach ([['dashboard', 'Dashboard'], ['wallets.index', 'Dompet'], ['transactions.index', 'Transaksi'], ['groups.index', 'Grup'], ['profile.index', 'Profil']] as [$routeName, $label])
                        <a href="{{ route($routeName) }}" class="block whitespace-nowrap rounded-lg px-3 py-2 text-sm font-medium {{ request()->routeIs($routeName === 'dashboard' ? 'dashboard' : Str::beforeLast($routeName, '.').'.*') ? 'bg-blue-600 text-white' : 'text-slate-600 hover:bg-blue-50 hover:text-blue-700' }}">
                            {{ $label }}
                        </a>
                    @endforeach
                </nav>
                <div class="border-t border-blue-100 p-4">
                    <button type="button" data-theme-toggle class="theme-toggle mb-3 flex w-full items-center justify-between rounded-lg border border-blue-200 px-3 py-2 text-sm font-medium text-blue-700 hover:bg-blue-50" aria-label="Ganti tema">
                        <span data-theme-label>Mode gelap</span><span aria-hidden="true">◐</span>
                    </button>
                    <div class="mb-4 hidden rounded-lg bg-blue-50 p-3 text-sm lg:block">
                        <p class="font-semibold text-blue-950">{{ auth()->user()->name }}</p>
                        <p class="truncate text-xs text-blue-700">{{ auth()->user()->email }}</p>
                    </div>
                    <form method="POST" action="{{ route('logout') }}" class="hidden lg:block">
                        @csrf
                        <button type="submit" class="w-full rounded-lg px-3 py-2 text-left text-sm font-medium text-blue-700 hover:bg-blue-50">Keluar</button>
                    </form>
                </div>
            </aside>
            <main class="min-w-0 flex-1 p-4 sm:p-6 lg:p-8">
                {{ $slot }}
            </main>
        </div>
    </body>
</html>
