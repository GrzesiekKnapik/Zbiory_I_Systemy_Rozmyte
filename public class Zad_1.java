import java.util.ArrayList;
import java.util.List;

public class Zad_1 {

    // Wspólny interfejs dla wszystkich typów zbiorów
    interface FuzzySet {
        double calculateMembership(double x);
        String getName();
    }

    // ZBIÓR TRÓJKĄTNY
    static class TriangleSet implements FuzzySet {
        private final double a, b, c;

        TriangleSet(double a, double b, double c) {
            this.a = a; this.b = b; this.c = c;
        }

        @Override
        public double calculateMembership(double x) {
            if (x <= a || x >= c) return 0;
            if (x == b) return 1.0;
            return (x < b) ? (x - a) / (b - a) : (c - x) / (c - b);
        }

        @Override
        public String getName() { return "Trójkątny (" + a + "," + b + "," + c + ")"; }
    }

    // ZBIÓR TRAPEZOWY
    static class TrapezoidSet implements FuzzySet {
        private final double a, b, c, d;

        TrapezoidSet(double a, double b, double c, double d) {
            this.a = a; this.b = b; this.c = c; this.d = d;
        }

        @Override
        public double calculateMembership(double x) {
            if (x <= a || x >= d) return 0;
            if (x >= b && x <= c) return 1.0;
            return (x < b) ? (x - a) / (b - a) : (d - x) / (d - c);
        }

        @Override
        public String getName() { return "Trapezowy (" + a + "," + b + "," + c + "," + d + ")"; }
    }

    // ZBIÓR GAUSSOWSKI (Funkcja dzwonowa)
    static class GaussianSet implements FuzzySet {
        private final double mean, sigma;

        GaussianSet(double mean, double sigma) {
            this.mean = mean;
            this.sigma = sigma;
        }
        
        @Override
        public double calculateMembership(double x) {
            // Wzór: e^(-(x-μ)² / (2σ²))
            return Math.exp(-Math.pow(x - mean, 2) / (2 * Math.pow(sigma, 2)));
        }

        @Override
        public String getName() { return "Gaussowski (μ=" + mean + ", σ=" + sigma + ")"; }
    }

    public static void main(String[] args) {
        // Przykładowe wartości testowe
        double[] testValues = {5.0, 7.5, 11.0};

        // Lista zbiorów do przetestowania
        List<FuzzySet> fuzzySets = new ArrayList<>();
        fuzzySets.add(new TriangleSet(2, 6, 10));
        fuzzySets.add(new TrapezoidSet(0, 7, 9, 12));
        fuzzySets.add(new GaussianSet(7, 1.5));

        // Wyświetlanie wyników w ładnej formie
        System.out.println("--- WYNIKI PRZYNALEŻNOŚCI DO ZBIORÓW ROZMYTYCH ---");
        
        for (double x : testValues) {
            System.out.println("\nDla x = " + x + ":");
            for (FuzzySet set : fuzzySets) {
                double result = set.calculateMembership(x);
                System.out.printf("- %-25s : %.4f%n", set.getName(), result);
            }
        }
    }
}