<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />

<script>
	(() => {
		const theme = localStorage.getItem('sakubijak-theme') || 'light';
		document.documentElement.classList.toggle('dark', theme === 'dark');
		document.documentElement.dataset.theme = theme;
		document.documentElement.style.colorScheme = `${theme} only`;
	})();
</script>

<title>{{ $title ?? 'SakuBijak' }}</title>

<link rel="preconnect" href="https://fonts.bunny.net">
<link href="https://fonts.bunny.net/css?family=instrument-sans:400,500,600" rel="stylesheet" />

@vite(['resources/css/app.css', 'resources/js/app.js'])
