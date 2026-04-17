import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *  AXIOM — Scientific Calculator
 *  A professional scientific calculator with graphing capabilities.
 *  Built with Java Swing.
 */
public class AXIOMCalculator extends JFrame {

    // ── Colour Palette ─────────────────────────────────────────────────────────
    static final Color C_BG        = new Color(12, 11, 22);
    static final Color C_PANEL     = new Color(26, 24, 46);
    static final Color C_DISPLAY   = new Color(10,  9, 20);
    static final Color C_BTN_NUM   = new Color(32, 30, 58);
    static final Color C_BTN_OP    = new Color(48, 26, 80);
    static final Color C_BTN_SCI   = new Color(20, 42, 74);
    static final Color C_BTN_EQ    = new Color(88, 48, 170);
    static final Color C_BTN_CLR   = new Color(150, 28, 62);
    static final Color C_BTN_MEM   = new Color(16, 60, 60);
    static final Color C_TEXT      = new Color(235, 232, 255);
    static final Color C_TEXT_DIM  = new Color(130, 125, 170);
    static final Color C_ACCENT    = new Color(140, 90, 240);
    static final Color C_CYAN      = new Color(56, 189, 210);
    static final Color C_GOLD      = new Color(250, 190, 60);
    static final Color C_GREEN     = new Color(60, 200, 130);

    // ── Fonts ──────────────────────────────────────────────────────────────────
    static final Font F_DISPLAY_BIG  = new Font("Consolas", Font.BOLD,  38);
    static final Font F_DISPLAY_EXPR = new Font("Consolas", Font.PLAIN, 13);
    static final Font F_BTN_MAIN     = new Font("Segoe UI",  Font.BOLD,  15);
    static final Font F_BTN_SCI      = new Font("Segoe UI",  Font.BOLD,  12);
    static final Font F_MODE         = new Font("Segoe UI",  Font.BOLD,  11);
    static final Font F_LABEL        = new Font("Segoe UI",  Font.PLAIN, 12);

    // ── Sub-panels ─────────────────────────────────────────────────────────────

    // ══════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new AXIOMCalculator().setVisible(true);
        });
    }

    public AXIOMCalculator() {
        super("AXIOM  —  Scientific Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(980, 700);
        setMinimumSize(new Dimension(820, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(C_BG);
        buildUI();
    }

    // ── Build top-level UI ─────────────────────────────────────────────────────
    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        add(buildHeader(), BorderLayout.NORTH);

        // Style tab bar BEFORE creating the pane
        UIManager.put("TabbedPane.selected",         C_PANEL);
        UIManager.put("TabbedPane.background",        C_BG);
        UIManager.put("TabbedPane.contentAreaColor",  C_BG);
        UIManager.put("TabbedPane.tabAreaBackground", C_BG);

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setBackground(C_BG);
        tabs.setForeground(C_TEXT);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));

        CalculatorPanel calcPanel  = new CalculatorPanel();
        GraphPanel      graphPanel = new GraphPanel();
        tabs.addTab("  \u229e  Calculator  ", calcPanel);
        tabs.addTab("  \u223f  Graph  ",       graphPanel);

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(28, 12, 58),
                    getWidth(), 0, new Color(8, 32, 72)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // bottom accent line
                g2.setColor(C_ACCENT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            }
        };
        h.setPreferredSize(new Dimension(0, 56));
        h.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));

        JLabel title = new JLabel("⬡  AXIOM");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(175, 130, 255));

        JLabel sub = new JLabel("Scientific Calculator  v2.0");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(C_CYAN);

        h.add(title, BorderLayout.WEST);
        h.add(sub,   BorderLayout.EAST);
        return h;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CALCULATOR PANEL
    // ══════════════════════════════════════════════════════════════════════════
    class CalculatorPanel extends JPanel {

        private JLabel  lblDisplay;
        private JLabel  lblExpr;
        private JLabel  lblMode;
        private JLabel  lblMemory;

        private StringBuilder input = new StringBuilder();
        private boolean resultShown  = false;
        private boolean isDeg        = true;
        private double  memory       = 0;
        private double  lastAns      = 0;

        CalculatorPanel() {
            setBackground(C_BG);
            setLayout(new BorderLayout(0, 4));
            setBorder(BorderFactory.createEmptyBorder(6, 8, 8, 8));
            add(buildDisplay(), BorderLayout.NORTH);
            add(buildButtons(), BorderLayout.CENTER);
        }

        // ── Display ────────────────────────────────────────────────────────────
        private JPanel buildDisplay() {
            JPanel dp = new JPanel(new BorderLayout()) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(C_DISPLAY);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                    // subtle inner glow
                    g2.setColor(new Color(100, 60, 200, 25));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 13, 13);
                }
            };
            dp.setPreferredSize(new Dimension(0, 118));
            dp.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
            dp.setOpaque(false);

            // Top row: mode + memory indicator
            JPanel topRow = new JPanel(new BorderLayout());
            topRow.setOpaque(false);

            lblMode = new JLabel("DEG");
            lblMode.setFont(F_MODE);
            lblMode.setForeground(C_CYAN);

            lblMemory = new JLabel("");
            lblMemory.setFont(F_MODE);
            lblMemory.setForeground(C_GOLD);

            topRow.add(lblMode,   BorderLayout.WEST);
            topRow.add(lblMemory, BorderLayout.EAST);

            lblExpr = new JLabel(" ");
            lblExpr.setFont(F_DISPLAY_EXPR);
            lblExpr.setForeground(C_TEXT_DIM);
            lblExpr.setHorizontalAlignment(SwingConstants.RIGHT);

            lblDisplay = new JLabel("0");
            lblDisplay.setFont(F_DISPLAY_BIG);
            lblDisplay.setForeground(C_TEXT);
            lblDisplay.setHorizontalAlignment(SwingConstants.RIGHT);

            dp.add(topRow,      BorderLayout.NORTH);
            dp.add(lblExpr,     BorderLayout.CENTER);
            dp.add(lblDisplay,  BorderLayout.SOUTH);
            return dp;
        }

        // ── Button grid ────────────────────────────────────────────────────────
        private JPanel buildButtons() {
            JPanel outer = new JPanel(new GridLayout(1, 2, 6, 0));
            outer.setBackground(C_BG);
            outer.add(buildSciGrid());
            outer.add(buildNumGrid());
            return outer;
        }

        private JPanel buildSciGrid() {
            JPanel p = new JPanel(new GridLayout(8, 3, 4, 4));
            p.setBackground(C_BG);

            String[][] sci = {
                { "sin",  "cos",  "tan"  },
                { "asin", "acos", "atan" },
                { "sinh", "cosh", "tanh" },
                { "log",  "ln",   "√"    },
                { "x²",   "xⁿ",   "∛"   },
                { "π",    "e",    "abs"  },
                { "(",    ")",    "mod"  },
                { "n!",   "EXP",  "DEG" },
            };

            for (String[] row : sci) {
                for (String lbl : row) {
                    Color fg = lbl.equals("DEG") ? C_GOLD : C_CYAN;
                    JButton b = makeButton(lbl, C_BTN_SCI, fg, F_BTN_SCI);
                    b.addActionListener(e -> onSci(lbl));
                    p.add(b);
                }
            }
            return p;
        }

        private JPanel buildNumGrid() {
            JPanel p = new JPanel(new GridLayout(7, 4, 4, 4));
            p.setBackground(C_BG);

            // Row 1: memory
            addBtn(p, "MC",  C_BTN_MEM,  C_GREEN, F_BTN_SCI,  e -> { memory = 0; updMem(); });
            addBtn(p, "MR",  C_BTN_MEM,  C_GREEN, F_BTN_SCI,  e -> push(fmtR(memory)));
            addBtn(p, "M+",  C_BTN_MEM,  C_GREEN, F_BTN_SCI,  e -> { memory += curDouble(); updMem(); });
            addBtn(p, "M-",  C_BTN_MEM,  C_GREEN, F_BTN_SCI,  e -> { memory -= curDouble(); updMem(); });
            // Row 2: util
            addBtn(p, "C",   C_BTN_CLR,  Color.WHITE, F_BTN_MAIN, e -> clear());
            addBtn(p, "±",   C_BTN_OP,   new Color(210,170,255), F_BTN_MAIN, e -> negate());
            addBtn(p, "%",   C_BTN_OP,   new Color(210,170,255), F_BTN_MAIN, e -> percent());
            addBtn(p, "⌫",  C_BTN_OP,   new Color(255,130,130), F_BTN_MAIN, e -> backspace());
            // Rows 3-5: digits + ops
            String[][] num = {
                {"7","8","9","÷"},
                {"4","5","6","×"},
                {"1","2","3","-"},
            };
            for (String[] row : num) {
                for (String lbl : row) {
                    boolean isOp = lbl.matches("[÷×-]");
                    Color bg = isOp ? C_BTN_OP : C_BTN_NUM;
                    Color fg = isOp ? new Color(210,170,255) : C_TEXT;
                    addBtn(p, lbl, bg, fg, F_BTN_MAIN, e -> onNum(lbl));
                }
            }
            // Row 6: 0 . = +
            addBtn(p, "0",   C_BTN_NUM,  C_TEXT,                F_BTN_MAIN, e -> onNum("0"));
            addBtn(p, ".",   C_BTN_NUM,  C_TEXT,                F_BTN_MAIN, e -> onNum("."));
            addBtn(p, "=",   C_BTN_EQ,   Color.WHITE,           F_BTN_MAIN, e -> calculate());
            addBtn(p, "+",   C_BTN_OP,   new Color(210,170,255),F_BTN_MAIN, e -> onNum("+"));
            // Row 7: ANS + extra
            addBtn(p, "ANS", C_BTN_SCI,  C_CYAN,  F_BTN_SCI, e -> push(fmtR(lastAns)));
            addBtn(p, "1/x", C_BTN_SCI,  C_CYAN,  F_BTN_SCI, e -> onSci("1/x"));
            addBtn(p, "π/2", C_BTN_SCI,  C_CYAN,  F_BTN_SCI, e -> push(fmtR(Math.PI/2)));
            addBtn(p, "2π",  C_BTN_SCI,  C_CYAN,  F_BTN_SCI, e -> push(fmtR(2*Math.PI)));

            return p;
        }

        // ── Button helpers ─────────────────────────────────────────────────────
        private void addBtn(JPanel p, String lbl, Color bg, Color fg, Font f, ActionListener al) {
            JButton b = makeButton(lbl, bg, fg, f);
            b.addActionListener(al);
            p.add(b);
        }

        JButton makeButton(String text, Color bg, Color fg, Font f) {
            JButton b = new JButton(text) {
                private float hover = 0f;
                { // init hover animation
                    addMouseListener(new MouseAdapter() {
                        @Override public void mouseEntered(MouseEvent e) {
                            javax.swing.Timer t = new javax.swing.Timer(12, null);
                            t.addActionListener(ae -> {
                                hover = Math.min(1f, hover + 0.12f);
                                repaint();
                                if (hover >= 1f) t.stop();
                            });
                            t.start();
                        }
                        @Override public void mouseExited(MouseEvent e) {
                            javax.swing.Timer t = new javax.swing.Timer(12, null);
                            t.addActionListener(ae -> {
                                hover = Math.max(0f, hover - 0.12f);
                                repaint();
                                if (hover <= 0f) t.stop();
                            });
                            t.start();
                        }
                    });
                }
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color base = bg;
                    Color light = bg.brighter();
                    Color c = interpolate(base, light, hover + (getModel().isPressed() ? 0.4f : 0f));
                    g2.setColor(c);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    // top highlight
                    g2.setColor(new Color(255,255,255, (int)(18 + hover*18)));
                    g2.fillRoundRect(1, 1, getWidth()-2, 4, 4, 4);
                    // text
                    g2.setFont(getFont());
                    g2.setColor(fg);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText())) / 2,
                        (getHeight() - fm.getHeight()) / 2 + fm.getAscent()
                    );
                }
                @Override protected void paintBorder(Graphics g) {}
                Color interpolate(Color a, Color b, float t) {
                    t = Math.min(1f, Math.max(0f, t));
                    return new Color(
                        (int)(a.getRed()   + (b.getRed()   - a.getRed()  ) * t),
                        (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                        (int)(a.getBlue()  + (b.getBlue()  - a.getBlue() ) * t)
                    );
                }
            };
            b.setFont(f);
            b.setForeground(fg);
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setPreferredSize(new Dimension(58, 46));
            return b;
        }

        // ── Actions ────────────────────────────────────────────────────────────
        private void onSci(String lbl) {
            switch (lbl) {
                case "sin":  appendFn("sin(");  break;
                case "cos":  appendFn("cos(");  break;
                case "tan":  appendFn("tan(");  break;
                case "asin": appendFn("asin("); break;
                case "acos": appendFn("acos("); break;
                case "atan": appendFn("atan("); break;
                case "sinh": appendFn("sinh("); break;
                case "cosh": appendFn("cosh("); break;
                case "tanh": appendFn("tanh("); break;
                case "log":  appendFn("log(");  break;
                case "ln":   appendFn("ln(");   break;
                case "√":    appendFn("sqrt("); break;
                case "∛":    appendFn("cbrt("); break;
                case "abs":  appendFn("abs(");  break;
                case "x²":   append("^2"); break;
                case "xⁿ":   append("^");  break;
                case "π":    append("π");  break;
                case "e":    append("e");  break;
                case "(":    append("(");  break;
                case ")":    append(")");  break;
                case "mod":  append("%");  break;
                case "EXP":  append("E");  break;
                case "1/x":  appendFn("1/("); break;
                case "n!":   factorial();  break;
                case "DEG":
                    isDeg = !isDeg;
                    lblMode.setText(isDeg ? "DEG" : "RAD");
                    lblMode.setForeground(isDeg ? C_CYAN : C_GOLD);
                    break;
            }
        }

        private void onNum(String lbl) {
            switch (lbl) {
                case "÷": append("/");  break;
                case "×": append("*");  break;
                default:
                    if (resultShown && lbl.matches("[0-9\\.]")) {
                        input.setLength(0);
                        resultShown = false;
                    }
                    append(lbl);
            }
        }

        private void appendFn(String fn) {
            if (resultShown) resultShown = false;
            input.append(fn);
            refreshDisplay();
        }

        private void append(String s) {
            if (resultShown) resultShown = false;
            input.append(s);
            refreshDisplay();
        }

        private void push(String val) {
            input.setLength(0);
            input.append(val);
            resultShown = true;
            refreshDisplay();
        }

        private void clear() {
            input.setLength(0);
            resultShown = false;
            lblDisplay.setText("0");
            lblExpr.setText(" ");
        }

        private void backspace() {
            if (input.length() > 0) {
                input.deleteCharAt(input.length()-1);
                refreshDisplay();
            }
        }

        private void negate() {
            String s = input.toString();
            if (s.isEmpty()) return;
            if (s.charAt(0) == '-') input.deleteCharAt(0);
            else input.insert(0, '-');
            refreshDisplay();
        }

        private void percent() {
            try {
                double v = Double.parseDouble(input.toString()) / 100.0;
                push(fmtR(v));
            } catch (Exception ignored) {}
        }

        private void factorial() {
            try {
                int n = (int) Double.parseDouble(input.toString());
                if (n < 0 || n > 20) { lblDisplay.setText("Domain Err"); return; }
                long r = 1; for (int i=2; i<=n; i++) r *= i;
                push(String.valueOf(r));
            } catch (Exception e) { lblDisplay.setText("Error"); }
        }

        private void calculate() {
            String expr = input.toString();
            if (expr.isEmpty()) return;
            lblExpr.setText(expr + "  =");
            try {
                double result = new ExpressionParser(expr, isDeg).parse();
                lastAns = result;
                String fmt = fmtR(result);
                input.setLength(0);
                input.append(fmt);
                lblDisplay.setFont(fmt.length() > 16 ? new Font("Consolas", Font.BOLD, 22)
                                                      : F_DISPLAY_BIG);
                lblDisplay.setText(fmt);
                resultShown = true;
            } catch (Exception e) {
                lblDisplay.setText("Syntax Error");
                input.setLength(0);
                resultShown = false;
            }
        }

        private void refreshDisplay() {
            String s = input.length() == 0 ? "0" : input.toString();
            lblDisplay.setFont(s.length() > 16 ? new Font("Consolas", Font.BOLD, 22)
                                               : F_DISPLAY_BIG);
            lblDisplay.setText(s);
        }

        private void updMem() {
            lblMemory.setText(memory == 0 ? "" : "M = " + fmtR(memory));
        }

        private double curDouble() {
            try { return Double.parseDouble(input.toString()); }
            catch (Exception e) { return 0; }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  GRAPH PANEL
    // ══════════════════════════════════════════════════════════════════════════
    class GraphPanel extends JPanel {

        private JTextField tfFunc, tfXMin, tfXMax, tfYMin, tfYMax;
        private GraphCanvas canvas;
        private final List<PlotEntry> plots = new ArrayList<>();
        private int colorIdx = 0;

        private final Color[] PLOT_COLORS = {
            new Color(140, 90, 240),
            new Color(56, 189, 210),
            new Color(240, 80, 110),
            new Color(60, 200, 130),
            new Color(250, 180, 50),
            new Color(200, 100, 60),
        };

        GraphPanel() {
            setBackground(C_BG);
            setLayout(new BorderLayout(0, 6));
            setBorder(BorderFactory.createEmptyBorder(6, 8, 8, 8));
            add(buildControls(), BorderLayout.NORTH);
            canvas = new GraphCanvas();
            add(canvas, BorderLayout.CENTER);
            add(buildHint(), BorderLayout.SOUTH);
        }

        private JPanel buildControls() {
            JPanel ctrl = new JPanel(new BorderLayout(0, 6)) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(C_PANEL);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
            };
            ctrl.setOpaque(false);
            ctrl.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

            // Function row
            JPanel funcRow = new JPanel(new BorderLayout(8, 0));
            funcRow.setOpaque(false);

            JLabel fxLbl = styledLabel("f(x) = ", C_ACCENT, new Font("Segoe UI", Font.BOLD, 15));

            tfFunc = styledField("sin(x)");
            tfFunc.addActionListener(e -> plot());

            JButton btnPlot  = graphBtn("Plot",      C_BTN_EQ);
            JButton btnClear = graphBtn("Clear All", C_BTN_CLR);
            btnPlot .addActionListener(e -> plot());
            btnClear.addActionListener(e -> { plots.clear(); colorIdx=0; canvas.repaint(); });

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            btns.setOpaque(false);
            btns.add(btnPlot); btns.add(btnClear);

            funcRow.add(fxLbl,  BorderLayout.WEST);
            funcRow.add(tfFunc, BorderLayout.CENTER);
            funcRow.add(btns,   BorderLayout.EAST);

            // Range row
            JPanel rng = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            rng.setOpaque(false);
            rng.add(styledLabel("x :", C_TEXT_DIM, F_LABEL));
            tfXMin = smallField("-10"); rng.add(tfXMin);
            rng.add(styledLabel("→",   C_TEXT_DIM, F_LABEL));
            tfXMax = smallField("10");  rng.add(tfXMax);
            rng.add(styledLabel("  y :", C_TEXT_DIM, F_LABEL));
            tfYMin = smallField("-10"); rng.add(tfYMin);
            rng.add(styledLabel("→",   C_TEXT_DIM, F_LABEL));
            tfYMax = smallField("10");  rng.add(tfYMax);
            JButton applyBtn = graphBtn("Apply", C_BTN_SCI);
            applyBtn.addActionListener(e -> canvas.repaint());
            rng.add(applyBtn);

            ctrl.add(funcRow, BorderLayout.NORTH);
            ctrl.add(rng,     BorderLayout.SOUTH);
            return ctrl;
        }

        private JLabel buildHint() {
            JLabel h = new JLabel(
              "  Tip: functions available — sin cos tan asin acos atan sinh cosh tanh sqrt cbrt log ln abs floor ceil exp  |  constants: π  e");
            h.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            h.setForeground(C_TEXT_DIM);
            return h;
        }

        private void plot() {
            String expr = tfFunc.getText().trim();
            if (expr.isEmpty()) return;
            Color col = PLOT_COLORS[colorIdx++ % PLOT_COLORS.length];
            plots.add(new PlotEntry(expr, col));
            canvas.repaint();
        }

        double xMin(){ try{return Double.parseDouble(tfXMin.getText());}catch(Exception e){return -10;} }
        double xMax(){ try{return Double.parseDouble(tfXMax.getText());}catch(Exception e){return  10;} }
        double yMin(){ try{return Double.parseDouble(tfYMin.getText());}catch(Exception e){return -10;} }
        double yMax(){ try{return Double.parseDouble(tfYMax.getText());}catch(Exception e){return  10;} }

        // ── Graph Canvas ───────────────────────────────────────────────────────
        class GraphCanvas extends JPanel {
            GraphCanvas() {
                setBackground(C_DISPLAY);
                setBorder(BorderFactory.createLineBorder(new Color(50, 46, 90), 1));
            }

            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                int W = getWidth(), H = getHeight();
                double xl = xMin(), xr = xMax(), yb = yMin(), yt = yMax();
                double xRng = xr - xl, yRng = yt - yb;

                drawGrid(g2, W, H, xl, xr, yb, yt, xRng, yRng);
                drawAxes(g2, W, H, xl, xr, yb, yt, xRng, yRng);
                for (PlotEntry pe : plots)
                    drawPlot(g2, W, H, pe, xl, xr, yb, yt, xRng, yRng);
                drawLegend(g2, W, H);
                drawAxisLabels(g2, W, H, xl, xr, yb, yt);
            }

            private void drawGrid(Graphics2D g2, int W, int H,
                                  double xl, double xr, double yb, double yt,
                                  double xRng, double yRng) {
                g2.setColor(new Color(30, 28, 52));
                g2.setStroke(new BasicStroke(0.5f));
                double xs = niceStep(xRng/10), ys = niceStep(yRng/10);
                for (double x = Math.ceil(xl/xs)*xs; x <= xr; x += xs)
                    g2.drawLine(toPixX(x,xl,xRng,W), 0, toPixX(x,xl,xRng,W), H);
                for (double y = Math.ceil(yb/ys)*ys; y <= yt; y += ys)
                    g2.drawLine(0, toPixY(y,yb,yRng,H), W, toPixY(y,yb,yRng,H));
            }

            private void drawAxes(Graphics2D g2, int W, int H,
                                  double xl, double xr, double yb, double yt,
                                  double xRng, double yRng) {
                g2.setStroke(new BasicStroke(1.5f));
                // X-axis
                if (yb <= 0 && yt >= 0) {
                    int ay = toPixY(0, yb, yRng, H);
                    g2.setColor(new Color(90, 86, 140));
                    g2.drawLine(0, ay, W, ay);
                }
                // Y-axis
                if (xl <= 0 && xr >= 0) {
                    int ax = toPixX(0, xl, xRng, W);
                    g2.setColor(new Color(90, 86, 140));
                    g2.drawLine(ax, 0, ax, H);
                }
            }

            private void drawAxisLabels(Graphics2D g2, int W, int H,
                                        double xl, double xr, double yb, double yt) {
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                g2.setColor(C_TEXT_DIM);
                double xRng = xr-xl, yRng = yt-yb;
                double xs = niceStep(xRng/10), ys = niceStep(yRng/10);
                int axisY = (yb<=0&&yt>=0) ? toPixY(0,yb,yRng,H) : H-4;
                int axisX = (xl<=0&&xr>=0) ? toPixX(0,xl,xRng,W) : 2;
                FontMetrics fm = g2.getFontMetrics();

                for (double x = Math.ceil(xl/xs)*xs; x <= xr; x += xs) {
                    if (Math.abs(x) < xs*0.01) continue;
                    String lbl = axisLbl(x);
                    int px = toPixX(x, xl, xRng, W);
                    g2.drawLine(px, axisY-3, px, axisY+3);
                    g2.drawString(lbl, px - fm.stringWidth(lbl)/2,
                        Math.min(axisY+14, H-2));
                }
                for (double y = Math.ceil(yb/ys)*ys; y <= yt; y += ys) {
                    if (Math.abs(y) < ys*0.01) continue;
                    String lbl = axisLbl(y);
                    int py = toPixY(y, yb, yRng, H);
                    g2.drawLine(axisX-3, py, axisX+3, py);
                    g2.drawString(lbl, Math.max(axisX+5, 2), py+4);
                }
            }

            private void drawPlot(Graphics2D g2, int W, int H, PlotEntry pe,
                                  double xl, double xr, double yb, double yt,
                                  double xRng, double yRng) {
                g2.setColor(pe.color);
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int steps = W * 3;
                Path2D path = new Path2D.Double();
                boolean started = false;
                double prevY = Double.NaN;

                for (int i = 0; i <= steps; i++) {
                    double x = xl + xRng * i / steps;
                    try {
                        double y = new ExpressionParser(pe.expr, true, x).parse();
                        if (!Double.isNaN(y) && !Double.isInfinite(y)) {
                            int px = toPixX(x, xl, xRng, W);
                            int py = toPixY(y, yb, yRng, H);
                            boolean jump = !Double.isNaN(prevY) && Math.abs(y-prevY) > yRng*1.5;
                            if (!started || jump) { path.moveTo(px,py); started=true; }
                            else                  { path.lineTo(px,py); }
                            prevY = y;
                        } else { started=false; prevY=Double.NaN; }
                    } catch (Exception ex) { started=false; prevY=Double.NaN; }
                }
                g2.draw(path);
            }

            private void drawLegend(Graphics2D g2, int W, int H) {
                if (plots.isEmpty()) return;
                int lh = 22, pad = 10;
                int legW = 210, legH = plots.size()*lh + pad*2;
                int lx = W - legW - 10, ly = 10;

                g2.setColor(new Color(16, 14, 30, 210));
                g2.fillRoundRect(lx, ly, legW, legH, 10, 10);
                g2.setColor(new Color(70, 65, 120));
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(lx, ly, legW, legH, 10, 10);

                g2.setFont(new Font("Consolas", Font.PLAIN, 11));
                for (int i = 0; i < plots.size(); i++) {
                    PlotEntry pe = plots.get(i);
                    int cy = ly + pad + i*lh + lh/2;
                    g2.setColor(pe.color);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawLine(lx+pad, cy, lx+pad+24, cy);
                    g2.setColor(C_TEXT);
                    String txt = "f(x)=" + (pe.expr.length()>16 ? pe.expr.substring(0,15)+"…" : pe.expr);
                    g2.drawString(txt, lx+pad+30, cy+4);
                }
            }

            int toPixX(double x, double xl, double xRng, int W) {
                return (int)((x-xl)/xRng*W);
            }
            int toPixY(double y, double yb, double yRng, int H) {
                return H - (int)((y-yb)/yRng*H);
            }
        }
    }

    static class PlotEntry {
        String expr; Color color;
        PlotEntry(String e, Color c){ expr=e; color=c; }
    }

    // ── Shared UI helpers ──────────────────────────────────────────────────────
    static JLabel styledLabel(String text, Color fg, Font f) {
        JLabel l = new JLabel(text); l.setForeground(fg); l.setFont(f); return l;
    }

    static JTextField styledField(String val) {
        JTextField tf = new JTextField(val);
        tf.setBackground(C_DISPLAY);
        tf.setForeground(C_TEXT);
        tf.setCaretColor(C_CYAN);
        tf.setFont(new Font("Consolas", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70,64,110), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return tf;
    }

    static JTextField smallField(String val) {
        JTextField tf = styledField(val);
        tf.setPreferredSize(new Dimension(58, 28));
        tf.setFont(new Font("Consolas", Font.PLAIN, 12));
        return tf;
    }

    static JButton graphBtn(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.setColor(Color.WHITE); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                    (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()-fm.getHeight())/2+fm.getAscent()
                );
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false); b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(90, 30));
        return b;
    }

    // ── Formatting ─────────────────────────────────────────────────────────────
    static String fmtR(double v) {
        if (Double.isNaN(v))       return "NaN";
        if (Double.isInfinite(v))  return v>0 ? "∞" : "-∞";
        if (v == Math.floor(v) && Math.abs(v) < 1e15)
            return String.valueOf((long) v);
        if (Math.abs(v) > 1e10 || (v != 0 && Math.abs(v) < 1e-6))
            return String.format("%.6e", v);
        return String.format("%.10f", v).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    static String axisLbl(double v) {
        if (v == Math.floor(v)) return String.valueOf((int)v);
        return String.format("%.1f", v);
    }

    static double niceStep(double rough) {
        double mag = Math.pow(10, Math.floor(Math.log10(rough)));
        double n = rough / mag;
        if (n < 1.5) return mag;
        if (n < 3.5) return 2*mag;
        if (n < 7.5) return 5*mag;
        return 10*mag;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  EXPRESSION PARSER  (recursive-descent, supports x as variable)
    // ══════════════════════════════════════════════════════════════════════════
    static class ExpressionParser {
        private final String raw;
        private final boolean deg;
        private final double  xVal;
        private int pos;

        ExpressionParser(String expr, boolean deg) { this(expr, deg, 0); }

        ExpressionParser(String expr, boolean deg, double xVal) {
            this.raw  = expr.trim()
                            .toLowerCase()
                            .replace("pi", "π")
                            .replace(" ", "");
            this.deg  = deg;
            this.xVal = xVal;
            this.pos  = 0;
        }

        double parse() {
            double r = expr();
            if (pos < raw.length()) throw new RuntimeException("Unexpected: " + raw.charAt(pos));
            return r;
        }

        // expr  = term ( ('+' | '-') term )*
        private double expr() {
            double r = term();
            while (pos < raw.length()) {
                char c = raw.charAt(pos);
                if      (c=='+') { pos++; r += term(); }
                else if (c=='-') { pos++; r -= term(); }
                else break;
            }
            return r;
        }

        // term  = factor ( ('*' | '/' | '%') factor )*
        private double term() {
            double r = factor();
            while (pos < raw.length()) {
                char c = raw.charAt(pos);
                if      (c=='*') { pos++; r *= factor(); }
                else if (c=='/') { pos++; r /= factor(); }
                else if (c=='%') { pos++; r %= factor(); }
                else break;
            }
            return r;
        }

        // factor = unary ('^' unary)?
        private double factor() {
            double r = unary();
            if (pos < raw.length() && raw.charAt(pos)=='^') {
                pos++;
                r = Math.pow(r, unary());
            }
            return r;
        }

        private double unary() {
            if (pos < raw.length() && raw.charAt(pos)=='-') { pos++; return -primary(); }
            if (pos < raw.length() && raw.charAt(pos)=='+') { pos++; }
            return primary();
        }

        private double primary() {
            if (pos >= raw.length()) throw new RuntimeException("Unexpected end");
            char c = raw.charAt(pos);

            // number
            if (Character.isDigit(c) || c=='.') return number();

            // grouped expression
            if (c=='(') { pos++; double r=expr(); eat(')'); return r; }

            // pi constant
            if (c=='π') { pos++; return Math.PI; }

            // variable x
            if (c=='x') { pos++; return xVal; }

            // named function / constant
            if (Character.isLetter(c)) return namedEntity();

            throw new RuntimeException("Unexpected char: " + c);
        }

        private double number() {
            int s = pos;
            while (pos < raw.length() && (Character.isDigit(raw.charAt(pos)) || raw.charAt(pos)=='.')) pos++;
            // scientific notation e.g. 1.5e10 or 2e-3
            if (pos < raw.length() && raw.charAt(pos)=='e' && pos+1 < raw.length()
                    && (Character.isDigit(raw.charAt(pos+1))
                        || raw.charAt(pos+1)=='-' || raw.charAt(pos+1)=='+')) {
                pos++;
                if (raw.charAt(pos)=='-' || raw.charAt(pos)=='+') pos++;
                while (pos < raw.length() && Character.isDigit(raw.charAt(pos))) pos++;
            }
            return Double.parseDouble(raw.substring(s, pos));
        }

        private double namedEntity() {
            int s = pos;
            while (pos < raw.length() && Character.isLetter(raw.charAt(pos))) pos++;
            String name = raw.substring(s, pos);

            // constant 'e' standing alone
            if (name.equals("e") && (pos >= raw.length() || raw.charAt(pos)!='('))
                return Math.E;

            // function call
            if (pos < raw.length() && raw.charAt(pos)=='(') {
                pos++; // eat '('

                // two-arg: pow(a,b)
                if (name.equals("pow")) {
                    double a = expr(); eat(','); double b = expr(); eat(')');
                    return Math.pow(a, b);
                }
                // two-arg: log(base,x) or single log(x)
                if (name.equals("log")) {
                    double a = expr();
                    if (pos < raw.length() && raw.charAt(pos)==',') {
                        pos++; double b = expr(); eat(')');
                        return Math.log(b) / Math.log(a);
                    }
                    eat(')');
                    return Math.log10(a);
                }

                double arg = expr();
                eat(')');
                return applyFn(name, arg);
            }
            throw new RuntimeException("Unknown: " + name);
        }

        private void eat(char expected) {
            if (pos < raw.length() && raw.charAt(pos)==expected) pos++;
        }

        private double applyFn(String n, double a) {
            double toR = deg ? Math.PI/180 : 1;
            double toD = deg ? 180/Math.PI : 1;
            switch (n) {
                case "sin":   return Math.sin(a*toR);
                case "cos":   return Math.cos(a*toR);
                case "tan":   return Math.tan(a*toR);
                case "asin": case "arcsin": return Math.asin(a)*toD;
                case "acos": case "arccos": return Math.acos(a)*toD;
                case "atan": case "arctan": return Math.atan(a)*toD;
                case "sinh":  return Math.sinh(a);
                case "cosh":  return Math.cosh(a);
                case "tanh":  return Math.tanh(a);
                case "sqrt":  return Math.sqrt(a);
                case "cbrt":  return Math.cbrt(a);
                case "ln":    return Math.log(a);
                case "abs":   return Math.abs(a);
                case "floor": return Math.floor(a);
                case "ceil":  return Math.ceil(a);
                case "round": return (double)Math.round(a);
                case "exp":   return Math.exp(a);
                case "sign": case "signum": return Math.signum(a);
                case "deg":   return Math.toDegrees(a);
                case "rad":   return Math.toRadians(a);
                default: throw new RuntimeException("Unknown function: " + n);
            }
        }
    }
}