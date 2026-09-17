<x-layouts.app.sidebar>
    <div class="space-y-6">
        <div class="flex flex-wrap items-end justify-between gap-4">
            <div>
                <p class="text-sm text-blue-600 dark:text-blue-400 font-medium">Ringkasan keuangan</p>
                <h1 class="text-2xl font-semibold text-zinc-900 dark:text-white">Dashboard</h1>
            </div>
            <a href="{{ route('transactions.index') }}" class="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600 transition-colors">Lihat transaksi</a>
        </div>
        
        @include('partials.flash')
        
        <div class="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
            @php
                $cards = [
                    ['Saldo personal', $personalBalance, 'text-blue-600 bg-blue-50 border-blue-100 dark:bg-blue-950/30 dark:border-blue-900/50'],
                    ['Saldo grup', $groupBalance, 'text-sky-600 bg-sky-50 border-sky-100 dark:bg-sky-950/30 dark:border-sky-900/50'],
                    ['Pemasukan bulan ini', $monthlyIncome, 'text-emerald-600 bg-emerald-50 border-emerald-100 dark:bg-emerald-950/30 dark:border-emerald-900/50'],
                    ['Pengeluaran bulan ini', $monthlyExpense, 'text-rose-600 bg-rose-50 border-rose-100 dark:bg-rose-950/30 dark:border-rose-900/50'],
                ];
            @endphp

            @foreach ($cards as [$label, $amount, $style])
                <div class="rounded-xl border border-blue-100 bg-white p-5 shadow-sm dark:border-zinc-800 dark:bg-zinc-900">
                    <p class="text-sm text-zinc-500 dark:text-zinc-400">{{ $label }}</p>
                    <p class="mt-2 text-2xl font-semibold {{ explode(' ', $style)[0] }}">Rp {{ number_format($amount, 0, ',', '.') }}</p>
                </div>
            @endforeach
        </div>

        <div class="grid gap-6 lg:grid-cols-[1.5fr_1fr]">
            <!-- Transaksi Terbaru -->
            <section class="rounded-xl border border-blue-100 bg-white shadow-sm dark:border-zinc-800 dark:bg-zinc-900">
                <div class="flex items-center justify-between border-b border-blue-50 px-5 py-4 dark:border-zinc-800">
                    <h2 class="font-semibold text-zinc-900 dark:text-white">Transaksi terbaru</h2>
                    <a class="text-sm font-medium text-blue-600 hover:text-blue-700 dark:text-blue-400 hover:underline" href="{{ route('transactions.index') }}">Semua</a>
                </div>
                <div class="divide-y divide-zinc-100 dark:divide-zinc-800">
                    @forelse ($recentTransactions as $transaction)
                        <a href="{{ route('transactions.show', $transaction->id) }}" class="flex items-center justify-between gap-4 px-5 py-4 hover:bg-blue-50/50 dark:hover:bg-zinc-800/50 transition-colors">
                            <div class="min-w-0">
                                <p class="truncate font-medium text-zinc-800 dark:text-zinc-200">{{ $transaction->description ?: 'Transaksi tanpa keterangan' }}</p>
                                <p class="text-xs text-zinc-500 dark:text-zinc-400">{{ $transaction->date?->format('d M Y') }} · {{ $transaction->wallet?->name }}</p>
                            </div>
                            <span class="shrink-0 font-semibold {{ $transaction->type === 'income' ? 'text-emerald-600 dark:text-emerald-400' : 'text-rose-600 dark:text-rose-400' }}">
                                {{ $transaction->type === 'income' ? '+' : '-' }} Rp {{ number_format($transaction->amount, 0, ',', '.') }}
                            </span>
                        </a>
                    @empty
                        <p class="px-5 py-8 text-center text-sm text-zinc-500 dark:text-zinc-400">Belum ada transaksi.</p>
                    @endforelse
                </div>
            </section>

            <!-- Akun Kamu -->
            <section class="rounded-xl border border-blue-100 bg-white p-5 shadow-sm dark:border-zinc-800 dark:bg-zinc-900">
                <h2 class="font-semibold text-zinc-900 dark:text-white">Akun kamu</h2>
                <dl class="mt-5 space-y-4 text-sm">
                    <div class="flex justify-between">
                        <dt class="text-zinc-500 dark:text-zinc-400">Dompet personal</dt>
                        <dd class="font-semibold text-blue-600 dark:text-blue-400">{{ $personalWalletsCount }}</dd>
                    </div>
                    <div class="flex justify-between">
                        <dt class="text-zinc-500 dark:text-zinc-400">Grup diikuti</dt>
                        <dd class="font-semibold text-blue-600 dark:text-blue-400">{{ $joinedGroupsCount }}</dd>
                    </div>
                </dl>
                <a href="{{ route('wallets.index') }}" class="mt-6 block rounded-lg border border-blue-200 bg-blue-50/50 px-4 py-2 text-center text-sm font-medium text-blue-700 hover:bg-blue-100 dark:border-blue-900/40 dark:bg-blue-950/30 dark:text-blue-300 dark:hover:bg-blue-900/50 transition-colors">Kelola dompet</a>
            </section>
        </div>
    </div>
</x-layouts.app.sidebar>