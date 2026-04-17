# ⬡ AXIOM Scientific Calculator

A professional, dark-themed scientific calculator with function graphing — built in Java Swing.

---

## 🚀 Quick Start

### Requirements
- Java Development Kit (JDK) 8 or higher  
  Download: https://adoptium.net

### Compile & Run

```bash
# 1. Compile
javac AXIOMCalculator.java

# 2. Run
java AXIOMCalculator
```

On Windows (double-click friendly):
```bat
javac AXIOMCalculator.java && java AXIOMCalculator
```

---

## ✨ Features

### 🔢 Calculator Tab
| Category | Functions |
|---|---|
| Trig | sin, cos, tan, asin, acos, atan, sinh, cosh, tanh |
| Power / Roots | x², xⁿ, √, ∛, 1/x |
| Logarithms | log (base-10), ln (natural) |
| Constants | π, e, π/2, 2π |
| Misc | abs, n!, mod, EXP (scientific notation) |
| Memory | M+, M-, MR, MC |
| Mode | DEG / RAD toggle |
| Extras | ANS (last answer), ± (negate), % |

### 📈 Graph Tab
- Plot up to 6 simultaneous functions in different colours
- Adjustable x and y range
- Auto-scaling grid with axis labels
- Discontinuity detection (no fake vertical lines on tan, 1/x, etc.)
- On-canvas legend

**Graph syntax examples:**
```
sin(x)
x^2 - 3*x + 2
sqrt(abs(x))
ln(x)
pow(x, 3)
cos(x) * exp(-0.1*x)
1/x
tan(x)
```

---

## 🎨 Design
- Deep dark navy / purple colour palette
- Animated button hover glow
- Custom rounded buttons (no system L&F artifacts)
- Gradient header bar with accent underline
- Responsive layout — resizable window

---

## 🛠 Architecture
```
AXIOMCalculator          (JFrame, entry point)
├── CalculatorPanel      (JPanel — scientific calc UI)
├── GraphPanel           (JPanel — graph controls + canvas)
│   └── GraphCanvas      (custom JPanel — renders plots)
└── ExpressionParser     (recursive-descent math parser)
```

The expression parser supports:
- Full operator precedence: `+  -  *  /  %  ^`
- Unary minus: `-sin(x)`
- Functions: sin cos tan asin acos atan sinh cosh tanh sqrt cbrt log ln abs floor ceil exp round sign deg rad
- Two-arg: `pow(x,n)`, `log(base, x)`
- Constants: `π` (or `pi`), `e`
- Variable: `x` (used for graphing)
- Scientific notation: `1.5e10`, `2e-3`
