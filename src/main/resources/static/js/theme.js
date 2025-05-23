// theme.js
function initializeTheme() {
    try {
        const savedTheme = localStorage.getItem('theme');
        if (savedTheme) {
            document.documentElement.setAttribute('data-theme', savedTheme);
        }
    } catch (e) {
        console.warn('Could not access localStorage:', e);
    }
    document.documentElement.classList.remove('loading');
}