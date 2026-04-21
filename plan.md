# Plan: EPMCDMETST-39731 – Add calculator memory functions (MC, MR, M+, M-)

## Feature scope
- Add 4 new memory buttons to the calculator UI: MC, MR, M+, M-.
  - Buttons must be visible, clickable, and styled consistently with existing buttons.
 - Memory value is maintained in-app (no persistence across reloads).
- Initialize memory to 0 on page load.
- Implement memory actions:
  - MR: replace the current display with the memory value
  - M+: add the current displayed numeric value to memory (no-op on Error)
  - M-: subtract the current displayed numeric value from memory (no-op on Error)
  - MC: reset memory to 0
- (Optional) Memory indicator ("M") visible only when memory != 0 (pending PO confirmation)

Non-goals
- Persisting memory across reloads (eg. localStorage)
- Adding more advanced memory functions (MS, M+M-, etc.)

## Design decisions
- Store memory as a number in script.js: let memory = 0;
- Defensive parsing for M+/M-:
  - If display is "Error", do nothing (AC #7)
  - If parse is NaN, treat as a no-op to avoid exceptions
- MR sets the display to the memory value as a string and does not trigger evaluation (do not auto-press =).
- Optional Memory indicator: toggle based on memory !== 0


## Tasks breakdown
UI / Markup
- Add buttons MC, MR, M+, M- to index.html
- Ensure placement does not break the existing grid
- (Optional) Add an "M" indicator element near the display

Styling
- Reuse existing button classes for consistent look & feel
- Adjust grid/spacing if needed

Logic
- Initialize memory on load
- implement handlers for MC, MR, M+, M-
- ensure M+/M- are no-op on Error and never throw
- (Optional) update memory indicator after each memory change

Verification
- Manually verify all AC items, including Error no-op behavior for M+/M-.

## Dependencies & risks
- Depends on existing display/input state management in script.js
- Edge cases: non-numeric display (empty, ., partial expression); make M+/M- a no-op
- Floating point precision with decimals
- Optional "M" indicator requirement unclear; confirm with PO
