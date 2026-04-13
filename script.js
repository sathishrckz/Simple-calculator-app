(() => {
  const screen = document.getElementById("screen");
  const historyEl = document.getElementById("history");
  const keys = document.querySelector(".keys");

  // State
  let current = "0"; // string being edited
  let previous = null; // number
  let operator = null; // '+', '-', '*', '/'
  let justEvaluated = false;

  const MAX_LEN = 16;

  function setScreen(value) {
    screen.value = value;
  }

  function setHistory(text) {
    historyEl.textContent = text;
  }

  function formatNumber(n) {
    if (!Number.isFinite(n)) return "Error";

    // Keep readable for typical calculator use
    const abs = Math.abs(n);
    if ((abs !== 0 && abs < 1e-6) || abs >= 1e12) {
      // scientific
      return n.toExponential(8).replace(/0+e/, "e").replace(/\.e/, "e");
    }

    // limit to 12 fractional digits to avoid floating noise
    const s = n.toFixed(12).replace(/\.0+$/, "").replace(/(\.\d*?)0+$/, "$1");
    return s;
  }

  function toNumber(str) {
    // handles '.', '-' etc safely
    if (str === "" || str === "-" || str === "." || str === "-.") return 0;
    return Number(str);
  }

  function clampLen(str) {
    if (str.length <= MAX_LEN) return str;
    // Try to keep end for decimals? We'll just cap.
    return str.slice(0, MAX_LEN);
  }

  function resetAll() {
    current = "0";
    previous = null;
    operator = null;
    justEvaluated = false;
    setHistory("");
    setScreen(current);
  }

  function inputDigit(d) {
    if (justEvaluated && operator == null) {
      // start new entry after '='
      current = "0";
      justEvaluated = false;
    }

    if (current === "0") {
      current = d;
    } else {
      current = clampLen(current + d);
    }
    setScreen(current);
  }

  function inputDecimal() {
    if (justEvaluated && operator == null) {
      current = "0";
      justEvaluated = false;
    }

    if (!current.includes(".")) {
      current = clampLen(current + ".");
      setScreen(current);
    }
  }

  function backspace() {
    if (justEvaluated) {
      // allow editing result
      justEvaluated = false;
    }

    if (current.length <= 1) {
      current = "0";
    } else {
      current = current.slice(0, -1);
      if (current === "-" || current === "") current = "0";
    }
    setScreen(current);
  }

  function applyPercent() {
    // Typical behavior: percent converts current to current/100
    const n = toNumber(current);
    const result = n / 100;
    current = formatNumber(result);
    justEvaluated = false;
    setScreen(current);
  }

  function compute(a, op, b) {
    switch (op) {
      case "+":
        return a + b;
      case "-":
        return a - b;
      case "*":
        return a * b;
      case "/":
        return b === 0 ? NaN : a / b;
      default:
        return b;
    }
  }

  function setOperator(nextOp) {
    const currNum = toNumber(current);

    if (previous == null) {
      previous = currNum;
    } else if (operator && !justEvaluated) {
      previous = compute(previous, operator, currNum);
      if (!Number.isFinite(previous)) {
        current = "Error";
        previous = null;
        operator = null;
        justEvaluated = true;
        setHistory("");
        setScreen(current);
        return;
      }
    }

    operator = nextOp;
    justEvaluated = false;
    current = "0";

    setHistory(`${formatNumber(previous)} ${prettyOp(operator)}`);
    setScreen(current);
  }

  function prettyOp(op) {
    return op === "*" ? "×" : op === "/" ? "÷" : op;
  }

  function equals() {
    if (operator == null || previous == null) return;

    const b = toNumber(current);
    const a = previous;
    const op = operator;

    const result = compute(a, op, b);

    setHistory(`${formatNumber(a)} ${prettyOp(op)} ${formatNumber(b)} =`);

    if (!Number.isFinite(result)) {
      current = "Error";
      previous = null;
      operator = null;
      justEvaluated = true;
      setScreen(current);
      return;
    }

    current = formatNumber(result);
    previous = null;
    operator = null;
    justEvaluated = true;
    setScreen(current);
  }

  function handleKey(action, value) {
    switch (action) {
      case "digit":
        inputDigit(value);
        break;
      case "decimal":
        inputDecimal();
        break;
      case "operator":
        setOperator(value);
        break;
      case "equals":
        equals();
        break;
      case "clear":
        resetAll();
        break;
      case "backspace":
        backspace();
        break;
      case "percent":
        applyPercent();
        break;
    }
  }

  // Click events
  keys.addEventListener("click", (e) => {
    const btn = e.target.closest("button");
    if (!btn) return;
    handleKey(btn.dataset.action, btn.dataset.value);
  });

  // Keyboard support
  window.addEventListener("keydown", (e) => {
    const k = e.key;

    if (k >= "0" && k <= "9") {
      e.preventDefault();
      inputDigit(k);
      return;
    }

    if (k === ".") {
      e.preventDefault();
      inputDecimal();
      return;
    }

    if (k === "Backspace") {
      e.preventDefault();
      backspace();
      return;
    }

    if (k === "Escape") {
      e.preventDefault();
      resetAll();
      return;
    }

    if (k === "Enter" || k === "=") {
      e.preventDefault();
      equals();
      return;
    }

    if (k === "+" || k === "-" || k === "*" || k === "/") {
      e.preventDefault();
      setOperator(k);
      return;
    }

    if (k === "%") {
      e.preventDefault();
      applyPercent();
    }
  });

  // Prevent manual typing; keep it display-only but still selectable.
  screen.addEventListener("beforeinput", (e) => e.preventDefault());
  screen.addEventListener("paste", (e) => e.preventDefault());

  // Init
  resetAll();
})();
