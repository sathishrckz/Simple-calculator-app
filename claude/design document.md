Design Document: Theme Toggle (Light/Dark) — EPMMCDMETST-39872
1. Overview
This document describes the design and implementation approach for adding a Light/Dark theme toggle to the Simple Calculator App.

Goals
Add a theme toggle in the calculator header to switch between light and dark modes.

Apply theme to app surfaces and interactive elements: page background, calculator card, display, buttons (including hover/active/focus states), and text colors.

Persist user selection in localStorage and restore on reload.

Keep calculator behavior unchanged (operations, input flow, history, keyboard support).

Accessibility: keyboard operable toggle, clear accessible name (e.g., aria-label for icon-only), and visible focus indication.

Non-goals
No changes to calculator math logic, history behavior, or existing keyboard shortcuts beyond ensuring no regressions.

No dependency on external theming libraries.

2. Proposed UX/UI
Toggle placement
Add a <button> in the calculator header (top bar) aligned to the right of the title (or alongside existing header controls).

If icon-only, include aria-label="Toggle theme" (or “Toggle dark mode”).

States
Default (Light): use light tokens.

Dark: use dark token overrides.

Focus: visible outline meeting contrast in both themes (e.g., outline: 2px solid var(--focus)).

Hover/Active: buttons and toggle must have theme-appropriate hover/active colors.

3. Architecture / Technical Design
Approach
Single source of truth at root: set a theme attribute on document.documentElement: document.documentElement.dataset.theme = 'light' | 'dark'.

CSS Variables (tokens) drive component colors, so switching theme is just swapping variable values.

Why root data-theme + CSS variables
Minimal CSS duplication.

Clear, scalable theming.

Easy to audit which properties are theme-driven.

4. Detailed Design
4.1 HTML changes (index.html)
Add a theme toggle button in the header:

<header class="calculator__header">
  <h1 class="calculator__title">Calculator</h1>
  <button
    class="theme-toggle"
    type="button"
    aria-label="Toggle theme"
  >
    <span class="theme-toggle__icon" aria-hidden="true">◐</span>
  </button>
</header>

Notes:

Use native <button> for built-in keyboard behavior (Enter/Space).

If later replaced with SVG icon, keep aria-label.

4.2 CSS changes (styles.css)
Token definition
:root {
  --bg: #f6f7fb;
  --surface: #ffffff;
  --fg: #111827;
  --muted: #6b7280;

  --display-bg: #f3f4f6;
  --display-fg: #111827;

  --btn-bg: #ffffff;
  --btn-fg: #111827;
  --btn-border: rgba(17, 24, 39, 0.15);
  --btn-hover: #f3f4f6;
  --btn-active: #e5e7eb;

  --accent: #2563eb;
  --focus: #2563eb;

  --shadow: 0 10px 25px rgba(0,0,0,0.08);
}

[data-theme="dark"] {
  --bg: #0b1220;
  --surface: #111827;
  --fg: #f9fafb;
  --muted: #9ca3af;

  --display-bg: #0f172a;
  --display-fg: #f9fafb;

  --btn-bg: #111827;
  --btn-fg: #f9fafb;
  --btn-border: rgba(249, 250, 251, 0.14);
  --btn-hover: #1f2937;
  --btn-active: #0b1220;

  --accent: #60a5fa;
  --focus: #60a5fa;

  --shadow: 0 10px 25px rgba(0,0,0,0.35);
}

Refactor existing rules to use tokens
body background/text: background: var(--bg); color: var(--fg);

Calculator card: background: var(--surface); box-shadow: var(--shadow);

Display: background: var(--display-bg); color: var(--display-fg);

Buttons: background: var(--btn-bg); color: var(--btn-fg); border-color: var(--btn-border);

Buttons hover/active: background: var(--btn-hover) / var(--btn-active)

Focus ring: :focus-visible { outline: 2px solid var(--focus); outline-offset: 2px; }

Theme toggle styles
.theme-toggle {
  background: transparent;
  border: 1px solid var(--btn-border);
  color: var(--fg);
  border-radius: 10px;
  padding: 6px 10px;
}

.theme-toggle:hover { background: var(--btn-hover); }
.theme-toggle:active { background: var(--btn-active); }

4.3 JavaScript changes (theme module / script.js)
Storage key
localStorage key: theme

Initialization logic
On DOMContentLoaded, read localStorage.theme:

If present, apply it.

If absent, optionally default to system preference via prefers-color-scheme.

Wrap storage in try/catch for edge cases where storage is unavailable.

const THEME_KEY = 'theme';
const root = document.documentElement;

function getPreferredTheme() {
  return window.matchMedia?.('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
}

function applyTheme(theme) {
  root.dataset.theme = theme;
}

function loadTheme() {
  try {
    const saved = localStorage.getItem(THEME_KEY);
    return saved || getPreferredTheme();
  } catch {
    return getPreferredTheme();
  }
}

function saveTheme(theme) {
  try { localStorage.setItem(THEME_KEY, theme); } catch {}
}

document.addEventListener('DOMContentLoaded', () => {
  const theme = loadTheme();
  applyTheme(theme);

  const btn = document.querySelector('.theme-toggle');
  btn?.addEventListener('click', () => {
    const next = root.dataset.theme === 'dark' ? 'light' : 'dark';
    applyTheme(next);
    saveTheme(next);
  });
});

Non-regression guarantee
No changes required to existing calculator operation handlers except adding theme init + toggle handler.

No custom keyboard handlers are necessary for the toggle button.

5. Testing Plan
Manual testing
Toggle Light/Dark and visually verify: background, card, display, buttons, text, hover/active/focus states.

Verify calculator operations and history rendering are unchanged.

Reload: theme persists and restores correctly.

Keyboard & accessibility
Tab to the toggle button; ensure focus is clearly visible.

Activate with Enter and Space.

Ensure the control has an accessible name (aria-label) when icon-only.

Quick contrast spot-check for text/buttons in dark theme.

Edge cases
localStorage unavailable: app still works and falls back to system preference.

prefers-color-scheme unsupported: default to light.

6. Dependencies & Risks
Dependencies
None beyond existing HTML/CSS/JS.

Risks and mitigations
Style refactor impacts layout: restrict changes to color/shadow properties only.

Contrast issues: validate token choices; adjust --fg/--btn-fg/--display-fg as needed.

Keyboard conflicts: keep toggle in natural tab order; do not stop event propagation.

7. Implementation Checklist
[ ] Add theme toggle button to header with aria-label.

[ ] Introduce CSS tokens and refactor existing colors to tokens.

[ ] Add dark theme overrides with [data-theme="dark"].

[ ] Implement theme init + toggle + persistence in JS.

[ ] Verify hover/active/focus states across themes.

[ ] Regression test calculator functions + keyboard support + history.