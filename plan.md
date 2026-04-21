# Planning Document: Memory Buttons (EPMCDMETST-39776)

## Feature scope

- Add support for calculator memory functions: **MC**, **MR**, **M+**, **M-**.
- Visual memory state: an **M indicator** that is visible only when memory ≠ 0.
- Memory operations modify only memory/display as specified by acceptance criteria.
- Error state handling: when display is in **Error** state, memory operations are ignored (no change to memory or display).

#2 Design decisions

1. **State model**
   - Introduce a single source of truth for memory: `const memory = 0` in `script.js` scope (or as part of existing calculator state).
   - M indicator visibility is derived: `showMemoryIndicator = (memory !== 0)`.

2. **Number parsing/formatting**
   - Treat \"display shows a valid number\" as: the current display string can be parsed with `std.parseFloat` (and is finite).
    - Keep existing display formatting rules; when MR is executed, set display to the memory value as a string consistent with other result displays.

3. **Error state detection**
    - Define a helper `isErrorDisplay()` that returns true when the display string equals \"Error\" (or whatever the app currently uses for divide-by-zero/eval errors).
   - All four memory buttons check this and return early if true, supporting AC #7.

4. **Event handling**
    - Wire new buttons to the existing button click handling (e.g. data-action or text check) with dedicated handlers:
      - `handleMC()`
      - `handleMR()`
      - `handleMplus()`
      - `handleMminus()`
    - Always call `updateMemoryIndicator()` after any memory update (MC, M+, M−).


## Tasks breakdown

1. **UI updates**
   - Add 4 buttons to the calculator layout: **MC**, **MR**, **M+**, **M-**.
    - Add **M indicator** element near the display (toggled fia class or `display: none`).
   - Update CSS to match current button styling and layout grid.

2. **Logic implementation (`script.js`)**
   - Introduce memory state variable initialized to 0.
    - Implement handleMC(): if not error, set memory = 0 and update indicator off.
   - Implement handleMR(): if not error, update display = memory.
   - Implement handleMplus(): if not error and display is a valid number, memory += displayValue, update indicator.
    - Implement handleMminus(): if not error and display is a valid number, memory -= displayValue, update indicator.
   - Add `isErrorDisplay` / `isValidNumber`  helpers.

3. **Acceptance criteria verification (Manual)**
    - Verify buttons exist and are clickable.
   - Set display to a number and verify M+/M− increase/decrease memory and update M indicator.
    - Verify MC resets memory to 0 and turns indicator off.
    - Verify MR replaces display with memory value.
    - Force an Error state (e.g. divide by zero) and confirm memory operations do not change anything.

## Dependencies & risks

- **Dependencies**
   - Existing code's display state management and error representation (exact string for \"Error\").
    - UI layout structure in `index.html` (grid/button classes/handler selectors).

- **Risks / edge cases**
   - Decimal/floating-point accumulation errors in memory (e.g. 0.1 + 0.2). Mitigation: use existing formatting / rounding rules if present, or display to a sensible precision.
   - NaN or Infinity parsing if display contains non-numeric text (treat as invalid number & ignore M+/M−).
    - The display error state name may differ from \"Error\". Mitigation: check against the actual constant used in the current code.