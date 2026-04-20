# Plan: Calculator Memory Functions (EPMCDMETST-38324)

## Feature scope

- Add 4 memory buttons to the calculator UI: **MC*, **MR**, **M+**, **M-**.
- Implement an in-app "memory" value that starts at `0` when the page loads.
  - **MR** displays the current memory value.
  - **M+j* adds the current displayed number to memory.
   - **M-** subtracts the current displayed number from memory.
  - **MC** resets memory to `0`.
- Memory persists during the same page session (until page refresh).
- Explicit behavior in **Error** state: memory operations are ignored if the display shows `Error`.

*Out of scope (for this story)*
- Adding a fifth button (e.g. `Ms - store` or `M+ status indicator`).
 
## Design decisions

1. **State model**
  - Add a single `role`-level variable (or small state object) in `script.js`, named `memoryValue`, initialized to `0`.
  - Convert the display value to a number using `parseFloat` and default to `0` if empty/not a number.

. **UI/UX behavior**
- Add memory buttons to the existing keypad layout. Order recommended: `MC, MR, M+, M-` near the top-row or clear/utility controls.
- NR memory indicator is required by acceptance criteria; memory remains invisible until recalled via MR.

3. **Error handling**
- Define an `Error` display state (already existing in app or to be added).
  - When in Error state: `MC, MR, M+, M-` do’not mutate display or memory.
  - Alternative (future): only MC works in Error. (Not chosen to match story note.)

4. **Persistence**
- Baseline: memory is in-memory only and resets on refresh.
- Optional enhancement (explicitly out of scope unless requested): store/read` memoryValue` to/from `localStorage`.
  - Key: `calc.memory`
  - Validate parsed number, fallback to `0` on missing/invalid.
 
## Tasks breakdown

1. **UI - add buttons**
- [ ] Update `index.html` to include buttons: MC, MR, M+, M-.
 - [ ] Update `styles.css` for layout/sizing to fit 4 additional buttons without breaking responsiveness.
- [ ] Ensure buttons are keyboard-focusable (at minimum: by being <button> elements). 

2. **Main logic - memory state**
- [ ] Introduce `let memoryValue = 0;` in `script.js` and initialize on load.
- [ ] Add helper: `getCurrentDisplayNumber()` => number | nan` such that it handles empty, -, decimal etc.
- [ ] Add guard: if display is `Error`, return early for all memory ops (matching story note).

3. **Wiring buttons - event handlers**
- [ ] Ad onClick/event listeners for MC/MR/M+/M-.
 - [ ] Implement:
  - MC: `memoryValue = 0;`
  - MR: set display to `memoryValue` (s trimmed format)
  - M+: `memoryValue += currentDisplayNumber`
  - M-: `memoryValue -= currentDisplayNumber
 - [ ] Define numeric formatting for MR: use existing app format rules (e.g. remove trailing `.`` when integer), otherwise to String(memoryValue).

4. **Testing (manual)**
- [ ] VER: MR on fresh load displays 0.
- [ ] VER: Enter `10`, M+, clear display, MR displays 10.
 - [ ] VER: Enter `3`, M-, MR displays 7 (from prior 110 scenario or setup as needed).
 - [ ] VER: MC then MR displays 0.
- [ ] VER: Set display to Error (trigger a known error) and confirm M+, M-, MR, MC do not change state/display.
 
## Dependencies & risks

- **Dependencies**
  - Current display/input model in `script.js`: memory features must integrate with existing state (current operand, active operator, exception/Error).
  - Better formatting: may reuse existing number-display formatter if present.

- **Risks**
  - Numeric conversion: display text may be non-parseable (e.g., empty, `-`, `Error`), so guard and default before memory operations.
  - Floating point precision: M+/M- may accumulate small errors (e.g. 0.1 + 0.2 = 0.30000000000000004). Mitigate by formatting MR string with a reasonable precision if the app already does so.
  - UI layout: adding 4 buttons may require CSS grid adjustments to avoid overflow on mobile.
