package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private int N;
    private int[] grids;
    private int openNum;
    private WeightedQuickUnionUF wqu;
    private WeightedQuickUnionUF wqu2;

    public Percolation(int N) {
       if (N <= 0) {
           throw new IllegalArgumentException();
       }
       this.N = N;
       openNum = 0;
       grids = new int[N * N + 2];
       wqu = new WeightedQuickUnionUF(N * N + 2);
       wqu2 = new WeightedQuickUnionUF(N * N + 1);
       for (int i = 0; i < N * N; i++) {
           grids[i] = -1;
       }
    }

    private int twoToNo(int row, int col) {
        return row * N + col;
    }

    public void open(int row, int col) {
        if (row < 0 || row >= N || col < 0 || col >= N) {
            throw new IllegalArgumentException();
        };
        int id = twoToNo(row, col);
        if (row == 0) {
            wqu.union(N * N, id);
            wqu2.union(N * N, id);
        }
        if (row == N - 1) {
            wqu.union(N * N + 1, id);
        }
        openNum++;
        grids[id] = id;

        int[][] direction = {
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1}
        };
        for (int i = 0; i < 4; i++) {
            int newX = row + direction[i][0];
            int newY = col + direction[i][1];
            if (newX < 0 || newX >= N || newY < 0 || newY >= N) {
                continue;
            }
            int newId = twoToNo(newX, newY);
            if (isOpen(newX, newY)) {
                wqu.union(id, newId);
                wqu2.union(id, newId);
            }
        }
    }

    public boolean isOpen(int row, int col) {
        if (row < 0 || row >= N || col < 0 || col >= N) {
            throw new IllegalArgumentException();
        };
        int id = twoToNo(row, col);
        return grids[id] >= 0;
    }

    public boolean isFull(int row, int col) {
        if (row < 0 || row >= N || col < 0 || col >= N) {
            throw new IllegalArgumentException();
        };
        int id = twoToNo(row, col);
        return wqu2.connected(N * N, id);
    }

    public int numberOfOpenSites() {
        return openNum;
    }

    public boolean percolates() {
        return wqu.connected(N * N, N * N + 1);
    }

    public static void main(String[] args) {

    }
}
