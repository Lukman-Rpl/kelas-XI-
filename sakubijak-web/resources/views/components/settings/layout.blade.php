<div class="flex items-start max-md:flex-col">
    <div class="mr-10 w-full pb-4 md:w-[220px]">
        <nav class="space-y-2">
            <a class="block rounded-lg px-3 py-2 text-blue-700 hover:bg-blue-50" href="{{ route('settings.profile') }}">Profile</a>
            <a class="block rounded-lg px-3 py-2 text-blue-700 hover:bg-blue-50" href="{{ route('settings.password') }}">Password</a>
            <a class="block rounded-lg px-3 py-2 text-blue-700 hover:bg-blue-50" href="{{ route('settings.appearance') }}">Appearance</a>
        </nav>
    </div>

    <hr class="border-blue-100 md:hidden">

    <div class="flex-1 self-stretch max-md:pt-6">
        <h2 class="text-xl font-semibold text-blue-950">{{ $heading ?? '' }}</h2>
        <p class="text-slate-600">{{ $subheading ?? '' }}</p>

        <div class="mt-5 w-full max-w-lg">
            {{ $slot }}
        </div>
    </div>
</div>
