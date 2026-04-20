# Planning Document: Calculator Memory Functions (EPMCDMETST-38324)

## 1) Feature scope
Implement calculator memory functions in the existing single-page calculator:

- Provide buttons: **MC, MR, M+, M-**.
- Maintain a memory register (default **0**) that can be updated via **M+ / M-** and reset via **MC**.
- **MR** outputs the current memory value to the display.
- Memory persists until page refresh; **include localStorage persistence in this story** (per decision).
- Error-state behavior: when display shows **Error** (e.g., divide-by-zero), **ignore** memory operations (MC/MR/M+/M- do nothing).

Out of scope:
- Memory indicator badge (e.g., “M” on screen)
- Keyboard shortcuts for memory functions

## 2) Design decisions

### State model
- Introduce a single state variable `memoryValue` (Number).
- Initialize from `localStorage` key `calc_memory` if present and valid; otherwise default to `0`.

### Persistence
- Persist memory on every update (MC, M+, M-) using `localStorage.setItem('calc_memory', ...)`.
- On app load, restore memory from localStorage; handle missing/invalid values gracefully.

### UI / interaction behavior
- **M+ / M-** operate on the current display value **only if** it parses to a finite number.
- **MR** writes memory value into the display (so follow-on operations use that value).
- **MC** resets memory to 0; display is **not** modified by MC.
- If display is **Error**, ignore all memory ops.

### Numeric rules
- Use JavaScript `number` for memory.
- Guard against `NaN` and `Infinity` when parsing display input so memory cannot be polluted.

## 3) Tasks breakdown

1. **UI: add buttons**
   - Update `index.html` to add **MC, MR, M+, M-** buttons in a suitable place (top/control row).
   - Ensure identifiers (`data-action` / values / classes) fit the existing click-handling pattern.
   - Update `styles.css` to match existing button sizing/spacing.

2. **Logic: implement memory ops**
   - Add `memoryValue` state in `script.js`.
   - Load initial value from localStorage (`calc_memory`).
   - Implement handlers:
     - **MC**: `memoryValue = 0` and persist
     - **M+**: `memoryValue += displayedNumber` and persist
     - **M-**: `memoryValue -= displayedNumber` and persist
     - **MR**: set display to `memoryValue`
   - Add guards:
     - If display is `Error`, ignore memory ops
     - If display value is not finite (for M+/M-), ignore

3. **Testing (manual)**
   - Default memory is 0; MR shows 0.
   - Display 5, M+, MR shows 5.
   - Display 2, M+, MR shows 7 (accumulates).
   - Display 3, M-, MR decreases by 3.
   - MC, MR shows 0.
   - Reload page: MR restores stored value from localStorage.
   - Force Error (e.g., divide by 0) then try MC/MR/M+/M-: no change, Error remains.

4. **Docs**
   - Update `README.md` with a short note about memory buttons and persistence.

## 4) Dependencies & risks

### Dependencies
- Existing DOM structure and click handling in `script.js` (we’ll attach memory actions consistently).
- Browser support for `localStorage` (standard for modern browsers).

### Risks
- **Parsing display value**: display may contain formatted text; ensure parsing is consistent with current app behavior.
- **Floating point precision**: JS number math can introduce rounding issues (acceptable for this simple calculator unless app already formats/rounds).
- **Error-state definition**: must align with how current app renders “Error”.
