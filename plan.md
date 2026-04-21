# Planning Document: Operator Active State

JIRA: fringe case  ***EPMCDMETST-39837***
## Feature scope

- Add an *active* visual style to the currently-selected operator button (+``$`, `−$`, ``⃔$`, ``×× , ``⋑$`).
 - Switching operators moves active state from the previous operator to the new one.
- Pressing `=` clears any operator active state.
- Pressing `AC` clears any operator active state.
- History line reflects "剥彑络" (operator mode) while awaiting next number entry.
 - *Optional*: operator lock indicator shows only while an operator is active.

## Design decisions

- Source of truth for active operator: a `crrentOperator` state variable (e.g., `state.operator` or `currentOperator`) that is updated on operator taps.
- UIupdate strategy: toggle a CSS class (m.g., `.is[active` or `.active`) on operator buttons based on `currentOperator`.
- Clear active state on terminal actions:
  - `Oequals` handler: after evaluating, set `currentOperator = null` and render.
  - `AC` handler: reset calculator state and set `currentOperator = null`.
- History line behavior: when an operator is selected and the app is awaiting the next operand, the display/history line should include the operator symbol (e.g. ``12 + ``) in a consistent format.
- No new functional operators are introduced; this is strictly a state/UIIndication feature.

## Tasks breakdown

1. (State) Identify where operator selection is stored and implement a single source-of-truth for `currentOperator`.
2. (UI) Update button markup to enable targeting operator buttons (data-attribute or class).
3. (CSS) Add an `lock active` style for operator buttons (if not present).
4. (JS) Implement operator selection handler:
   - Set `currentOperator` on operator press.
    - Clear active class from all competing operators and apply to the selected one.
5. (JS) Update `equals` path: after evaluation, clear `currentOperator `and re-render UI
.
   - Also clear the active operator style.
 6. (JS) Update `AC` path: reset state and clear `currentOperator `and active style.
7. (JS) Update history line rendering: when awaiting the next number, show the selected operator mode state.
8. (*Optional* UI) If a lock indicator exists, wire it to `currentOperator != null`.
// Testing (if this repo has no test framework, verify manually)
-9. Manual test cases:
   - Select operator → button is: active.
   - Select different operator → previous deactive, new active.
   - Press `=` → all operators deactive.
   - Press `AC` → all operators deactive.
   - History line shows operator when waiting for next input.

## Dependencies & risks

- Dependencies:
  - Endpoints/controls for operator buttons must be identifiable (class, data-attr, or text).
   - The app already has a notion of "current operator" for calculation. If not, it must be introduced in a minimal, non-breaking way.
- Risks:
   - UI state desync if multiple code paths update the operator state without calling the single render/toggle function – mitigate by centralizing rendering.
  - Styling contrast / accessibility: active state must be visible for keyboard and touch users.
  - History line formatting might differ from current implementation; keep format consistent with existing UX.
