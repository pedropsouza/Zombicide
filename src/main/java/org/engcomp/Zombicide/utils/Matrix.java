package org.engcomp.Zombicide.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Matrix<T> {
    private int cols,rows;
    protected T[][] mat;

    public Matrix(int cols, int rows, List<T> data) {
        initFromList(cols, rows, data);
    }

    public Matrix(Stream<MatrixEntry> data) {
        ArrayList<ArrayList<T>> dyndata = new ArrayList<>();
        data.forEach(e -> {
            var row = dyndata.get(e.idx.row);
            row.set(e.idx.col, e.val);
        });
        var l = dyndata.stream().flatMap(Collection::stream).toList();
        initFromList(dyndata.getFirst().size(), dyndata.size(), l);
    }

    private void initFromList(int cols, int rows, List<T> data) {
        this.cols = cols; this.rows = rows;
        this.mat = (T[][])new Object[rows][cols];
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                mat[y][x] = data.get(y*cols + x);
            }
        }
    }

    public T get(int col, int row) {
        try {
            return this.mat[row][col];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public T get(MatrixIdx idx) {
        return this.mat[idx.row][idx.col];
    }

    public void set(int col, int row, T val) {
        this.mat[row][col] = val;
    }
    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public MatrixEntry getEntry(MatrixIdx idx) {
        return new MatrixEntry(idx, get(idx));
    }

    public String stringSerialize(Function<T, String> serFunc)
            throws IOException {
        StringBuilder data = new StringBuilder(
                String.valueOf(cols) +
                " " +
                String.valueOf(rows) +
                "\n");
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                data.append(serFunc.apply(mat[y][x]));
                data.append((x == cols - 1)? "\n" : " ");
            }
        }
        return data.toString();
    }

    public static <T> Matrix<T> stringDeserialize(Class<T> dataClass, Function<String,T> deserFunc, String in)
            throws IOException, ClassNotFoundException {
        var lines = in.lines().iterator();
        var dims_line = lines.next().split(" ");
        int cols = Integer.parseInt(dims_line[0], 10);
        int rows = Integer.parseInt(dims_line[1], 10);
        T[] mat = (T[])new Object[rows*cols];
        for (int y = 0; y < rows; y++) {
            var line = lines.next();
            var values = line.split(" ");
            for (int x = 0; x < cols; x++) {
                mat[y*cols + x] = deserFunc.apply(values[x]);
            }
        }
        return new Matrix<>(cols, rows, List.of(mat));
    }

    public static class MatrixIdx {
        public int col, row;
        public MatrixIdx(int col, int row) {
            this.col = col;
            this.row = row;
        }
    }
    public class MatrixEntry {
        public MatrixIdx idx; public T val;
        public MatrixEntry(MatrixIdx idx, T val) {
            this.idx = idx;
            this.val = val;
        }
    }
    public Stream<MatrixEntry> stream() {
        AtomicInteger i = new AtomicInteger();
        Supplier<MatrixIdx> idxs = () -> {
            var midx = new MatrixIdx(i.get() % this.cols, i.get() / this.cols);
            i.getAndIncrement();
            return midx;
        };
        return Stream.generate(idxs).limit(this.cols*this.rows).map(idx -> new MatrixEntry(idx, get(idx)));
    }

    @Override
    public String toString() {
        try {
            return this.stringSerialize(Object::toString);
        } catch(Exception e) {
            return "couldn't render matrix: " + e.toString();
        }
    }
}
