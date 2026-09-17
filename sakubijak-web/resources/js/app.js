document.addEventListener('click', (event) => {
	const toggle = event.target.closest('[data-theme-toggle]');

	if (!toggle) {
		return;
	}

	const nextTheme = document.documentElement.classList.contains('dark') ? 'light' : 'dark';
	document.documentElement.classList.toggle('dark', nextTheme === 'dark');
	document.documentElement.dataset.theme = nextTheme;
	document.documentElement.style.colorScheme = `${nextTheme} only`;
	localStorage.setItem('sakubijak-theme', nextTheme);
	document.querySelectorAll('[data-theme-label]').forEach((label) => {
		label.textContent = nextTheme === 'dark' ? 'Mode terang' : 'Mode gelap';
	});
});
