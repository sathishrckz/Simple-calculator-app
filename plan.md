# Planning Document — EPMMCDMETST-39872: Theme Toggle (Light/Dark)

## Feature scope
- Add a theme toggle in the calculator header to switch between light and dark modes.
- Apply theme to app surfaces and interactive elements: page background, calculator card, display, buttons (including hover/active/focus states), and text colors.
- Persist user selection in localStorage and restore on page reload.
- Ensure calculator behavior remains unchanged: math operations, input flow, history rendering, and keyboard support.
- Accessibility: toggle must be keyboard operable, have a clear accessible name (e.g. aria-label) if icon-only, and have visible focus indication.

## Design decisions
1. **Theming mechanism**: Add a theme attribute on the root element (`document.documentElement.dataset.theme = "dark"| "light"`), or toggle a root class (e.g. `.dark`). Prefer single source of truth at the root for simple CSS selectors.
-2. **SSS strategy**: Use CSS custom properties (variables) for colors so the theme switch is a variable set change rather than drastic style replication.
   - Define light tokens in `:root` and dark tokens in `[data-theme="dark"]` or `.dark`.
   - Update existing styles to reference tokens (e.g. `background-color: var(--bg)` instead of hard-coded colors).
3. **Persistence**: Save selection under `theme` key in localStorage. On load, apply stored theme if present; if absent, optionally default to system preference (`window.matchMedia('(prefers-color-scheme: dark)'))`).
-4. **Toggle UI**: Use a real `<button>` element in the header.
   - If icon-only: add `aria-label="Toggle dark mode"` (or similar).
   - Ensure roles are native (no need for `role="button"`) and the control is focusable.
   - Keyboard: default button behavior supports Enter/Space; no custom key handling unless needed.
-5. **Non-regression**: Limit changes to styling and a small theme init/toggle module; avoid changing calculator logic in script.js except to initialize theme.

## Tasks breakdown
1. **UI: Add toggle control**
- Update header markup (in `index.html`) to include a toggle button in the calculator header.
   - Add accessible name (aria-label) if icon-only.
    - Add focus style (e.g. `outline`) that meets contrast.
2. **CSS: Theme tokens and application**
   - Introduce CSS variables for colors (bg, fg, button bg, button fg, accent, border/shadow) in `styles.css`.
   - Add dark theme overrides under `[data-theme="dark"]` (corresponding tokens values).
   - Refactor existing selectors to use variables so all components update on toggle.
    - Verify hover/active/focus states in both themes.
3. **JS: Theme toggle logic + persistence**
    - On DOMContentLoaded: read `localStorage.theme`, apply to root.
    - On toggle click: flip theme, update root attribute/class, write to localStorage.
    - Ensure no interference with existing keyboard handlers/event listeners.
4. **Testing / verification**
    - Manual: toggle light/dark, operate calculator, check history display, reaload restores theme.
    - Keyboard: Tab to toggle, activate with Enter/Space, ensure focus visible, calc keyboard still works.
    - Accessibility: aria-label present if icon-only; contrast check quickly for text/buttons in dark mode.

## Dependencies & risks
- **Dependencies**: None beyond existing HTML/CSS/JS. If using `(prefers-color-scheme)`, depends on browser support (modern browsers ON).
- **Risks**:
   - Style refactor may unintentionally change layout/spacing if selectors are touched — keep changes to color/shadow properties only.
   - Insufficient contrast in dark theme colors – verify text/button contrast.
   - LocalStorage disabled/unhandled errors (e.g. private mode edge cases) – wrap localStorage access in try/catch if needed.
- Keyboard handling conflicts between toggle and calculator shortcuts – ensure toggle is in natural Taborder and does not stop propagation unless intentional.
