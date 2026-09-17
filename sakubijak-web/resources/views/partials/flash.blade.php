@if (session('success') || session('info') || session('error'))
    <div class="space-y-2">
        @foreach (['success' => 'emerald', 'info' => 'sky', 'error' => 'rose'] as $type => $color)
            @if (session($type))
                <div class="rounded-lg border border-{{ $color }}-200 bg-{{ $color }}-50 px-4 py-3 text-sm text-{{ $color }}-800 dark:border-{{ $color }}-900 dark:bg-{{ $color }}-950 dark:text-{{ $color }}-200">
                    {{ session($type) }}
                </div>
            @endif
        @endforeach
    </div>
@endif
@if ($errors->any())
    <div class="rounded-lg border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-800 dark:border-rose-900 dark:bg-rose-950 dark:text-rose-200">
        <ul class="list-inside list-disc">
            @foreach ($errors->all() as $error)
                <li>{{ $error }}</li>
            @endforeach
        </ul>
    </div>
@endif