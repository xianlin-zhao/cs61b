package hw2;

public class PercolationStats {
    private int N;
    private int T;
    private double mu;
    private double dev;
    private double thresholds[];

    public PercolationStats(int N, int T, PercolationFactory pf) {
        if (N <= 0 || T <= 0) {
            throw new IllegalArgumentException();
        }
        this.N = N;
        this.T = T;
        thresholds = new double[T];
        for (int i = 0; i < T; i++) {
            int randX = 0;
            int randY = 0;
            Percolation per = pf.make(N);
            while (!per.percolates()) {
                randX = edu.princeton.cs.introcs.StdRandom.uniform(0, N);
                randY = edu.princeton.cs.introcs.StdRandom.uniform(0, N);
                per.open(randX, randY);
            }
            thresholds[i] = per.numberOfOpenSites() / (N * N);
        }
        mu = edu.princeton.cs.introcs.StdStats.mean(thresholds);
        dev = edu.princeton.cs.introcs.StdStats.stddev(thresholds);
    }

    public double mean() {
        return mu;
    }

    public double stddev() {
        return dev;
    }

    public double confidenceLow() {
        return mu - (1.96 * dev / Math.sqrt(T));
    }

    public double confidenceHigh() {
        return mu + (1.96 * dev / Math.sqrt(T));
    }
}
