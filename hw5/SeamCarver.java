import edu.princeton.cs.algs4.Picture;

import java.awt.*;

public class SeamCarver {
    private Picture picture;
    private boolean isVertical;
    private int width;
    private int height;

    public SeamCarver(Picture picture) {
        this.picture = new Picture(picture);
        isVertical = true;
        width = picture.width();
        height = picture.height();
    }

    public Picture picture() {
        return new Picture(picture);
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public double energy(int x, int y) {
        double rx, gx, bx, ry, gy, by;
        Color left, right, up, down;
        if (isVertical) {
            left = picture.get((x - 1 + width) % width, y);
            right = picture.get((x + 1) % width, y);
            up = picture.get(x, (y - 1 + height) % height);
            down = picture.get(x, (y + 1) % height);
        } else {
            left = picture.get(x, (y - 1 + height) % height);
            right = picture.get(x, (y + 1) % height);
            up = picture.get((x - 1 + width) % width, y);
            down = picture.get((x + 1) % width, y);
        }
        rx = left.getRed() - right.getRed();
        gx = left.getGreen() - right.getGreen();
        bx = left.getBlue() - right.getBlue();
        ry = up.getRed() - down.getRed();
        gy = up.getGreen() - down.getGreen();
        by = up.getBlue() - down.getBlue();
        double dx = rx * rx + gx * gx + bx * bx;
        double dy = ry * ry + gy * gy + by * by;
        return dx + dy;
    }

    private double getMin(int i, int j, int[][] path, double[][] dp) {
        double[] v = new double[3];
        v[1] = dp[i][j - 1];
        if (i > 0) {
            v[0] = dp[i - 1][j - 1];
        } else {
            v[0] = Double.MAX_VALUE;
        }
        if (i < width - 1) {
            v[2] = dp[i + 1][j - 1];
        } else {
            v[2] = Double.MAX_VALUE;
        }
        double ans = Double.MAX_VALUE;
        int pos = 0;
        for (int k = 0; k < 3; k++) {
            if (v[k] < ans) {
                pos = k;
                ans = v[k];
            }
        }
        path[i][j - 1] = pos + i - 1;
        return ans;
    }

    public int[] findHorizontalSeam() {
        isVertical = false;
        swap();
        int[] ans = findVerticalSeam();
        swap();
        isVertical = true;
        return ans;
    }

    private void swap() {
        int tmp = width;
        width = height;
        height = tmp;
    }

    public int[] findVerticalSeam() {
        int[] ans = new int[height];
        double[][] dp = new double[width][height];
        int[][] path = new int[width][height];

        for (int i = 0; i < width; i++) {
            double e = isVertical ? energy(i, 0) : energy(0, i);
            dp[i][0] = e;
            path[i][height - 1] = i;
        }

        for (int j = 1; j < height; j++) {
            for (int i = 0; i < width; i++) {
                double e = isVertical ? energy(i, j) : energy(j, i);
                dp[i][j] = e + getMin(i, j, path, dp);
            }
        }

        double min = Double.MAX_VALUE;
        int pos = 0;
        for (int i = 0; i < width; i++) {
            if (dp[i][height - 1] < min) {
                min = dp[i][height - 1];
                pos = i;
            }
        }

        for (int j = height - 1; j >= 0; j--) {
            ans[j] = path[pos][j];
            pos = ans[j];
        }
        return ans;
    }

    private boolean isValid(int[] seam) {
        for (int i = 0, j = 1; j < seam.length; i++, j++) {
            if (Math.abs(seam[i] - seam[j]) > 1) {
                return false;
            }
        }
        return true;
    }

    public void removeHorizontalSeam(int[] seam) {
        if (seam.length != width || !isValid(seam)) {
            throw new IllegalArgumentException();
        }
        SeamRemover.removeHorizontalSeam(picture, seam);
    }

    public void removeVerticalSeam(int[] seam) {
        if (seam.length != height || !isValid(seam)) {
            throw new IllegalArgumentException();
        }
        SeamRemover.removeVerticalSeam(picture, seam);
    }
}
