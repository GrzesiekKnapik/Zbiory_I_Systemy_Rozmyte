import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Main extends JFrame {

    // --- DANE I STATYSTYKI ---
    static String[] featureNames = {"Sepal Length", "Sepal Width", "Petal Length", "Petal Width"};
    static String[] classNames = {"Setosa", "Versicolor", "Virginica"};
    static Color[] classColors = {new Color(255, 50, 50), new Color(50, 200, 50), new Color(50, 50, 255)};

    // Średnie (Mean)
    static double[][] means = {
            {5.00, 3.42, 1.46, 0.24}, // setosa
            {5.93, 2.77, 4.26, 1.32}, // versicolor
            {6.58, 2.97, 5.55, 2.02}  // virginica
    };

    // Odchylenia (Std Dev)
    static double[][] stds = {
            {0.35, 0.37, 0.17, 0.10},
            {0.51, 0.31, 0.46, 0.19},
            {0.63, 0.32, 0.55, 0.27}
    };

    // --- FUNKCJE PRZYNALEŻNOŚCI ---

    public static double gauss(double x, double m, double s) {
        return Math.exp(-Math.pow(x - m, 2) / (2 * Math.pow(s, 2)));
    }

    public static double triangular(double x, double a, double b, double c) {
        if (x <= a || x >= c) return 0;
        if (x <= b) return (x - a) / (b - a);
        return (c - x) / (c - b);
    }

    public static double trapezoidal(double x, double a, double b, double c, double d) {
        if (x <= a || x >= d) return 0;
        if (x >= b && x <= c) return 1;
        if (x < b) return (x - a) / (b - a);
        return (d - x) / (d - c);
    }

    // --- KONSTRUKTOR OKNA ---

    public Main() {
        setTitle("Klasyfikator Rozmyty Iris - Statystyki i Wykresy");
        setSize(1200, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrowanie na ekranie
        setLayout(new GridLayout(2, 2, 10, 10)); // Siatka 2x2 z odstępami

        for (int i = 0; i < 4; i++) {
            add(new ChartPanel(i));
        }
    }

    // --- PANEL WYKRESU (SIATKA 2x2) ---

    class ChartPanel extends JPanel {
        int featureIdx;

        public ChartPanel(int idx) {
            this.featureIdx = idx;
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int margin = 50;

            // Rysowanie siatki i osi
            g2.setColor(new Color(230, 230, 230));
            for(int i=0; i<=10; i++) { // Linie poziome
                int y = h - margin - (i * (h - 2 * margin) / 10);
                g2.drawLine(margin, y, w - margin, y);
            }

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(margin, h - margin, w - margin, h - margin); // Oś X
            g2.drawLine(margin, margin, margin, h - margin);         // Oś Y

            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString(featureNames[featureIdx], w / 2 - 40, h - 10);
            g2.drawString("f(x)", 10, margin);

            // Zakres wizualizacji
            double xMin = 0, xMax = 8;

            // Rysowanie 3 klas
            for (int c = 0; c < 3; c++) {
                g2.setColor(classColors[c]);
                Path2D.Double path = new Path2D.Double();

                double m = means[c][featureIdx];
                double s = stds[c][featureIdx];

                boolean first = true;
                for (int px = margin; px < w - margin; px++) {
                    double xVal = xMin + (double) (px - margin) / (w - 2 * margin) * (xMax - xMin);

                    // MOŻESZ TUTAJ ZMIENIĆ FUNKCJĘ NA triangular LUB trapezoidal
                    double yVal = gauss(xVal, m, s);

                    int py = (int) (h - margin - yVal * (h - 2 * margin));

                    if (first) {
                        path.moveTo(px, py);
                        first = false;
                    } else {
                        path.lineTo(px, py);
                    }
                }
                g2.setStroke(new BasicStroke(2.5f));
                g2.draw(path);

                // Legenda wewnątrz wykresu
                g2.drawString(classNames[c], w - 100, margin + (c * 20));
            }
        }
    }

    // --- MAIN / KLASYFIKATOR ---

    public static void main(String[] args) {
        // 1. Uruchomienie okna
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));

        // 2. Logika klasyfikatora
        double[] testSample = {5.4, 3.9, 1.7, 0.4}; // Próbka (Setosa)
        System.out.println("========================================");
        System.out.println("KLASYFIKATOR ROZMYTY - WYNIKI");
        System.out.println("Próbka wejściowa: " + Arrays.toString(testSample));
        System.out.println("========================================");

        double maxScore = -1;
        String bestClass = "";

        for (int c = 0; c < 3; c++) {
            double currentScore = 0;
            System.out.println("Klasa: " + classNames[c]);

            for (int f = 0; f < 4; f++) {
                double membership = gauss(testSample[f], means[c][f], stds[c][f]);
                currentScore += membership;
                System.out.printf("  - %-15s: %.4f%n", featureNames[f], membership);
            }

            System.out.printf("  SUMA PRZYNALEŻNOŚCI: %.4f%n%n", currentScore);

            if (currentScore > maxScore) {
                maxScore = currentScore;
                bestClass = classNames[c];
            }
        }

        System.out.println("----------------------------------------");
        System.out.println("PRZEWIDZIANA KLASA: " + bestClass.toUpperCase());
        System.out.println("----------------------------------------");
    }
}