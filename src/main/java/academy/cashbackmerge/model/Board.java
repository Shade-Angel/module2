package academy.cashbackmerge.model;

import academy.cashbackmerge.engine.GameEngine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Board {
    private final int constcol = 4;
    private final int constrol = 4;
    private final int sizeBoard = 4;

    private final int[][] grid = new int[constcol][constrol];

    public Board() {}

    public Board(Board other) {
        for (int r = 0; r < constrol; r++) {
            System.arraycopy(other.grid[r], 0, this.grid[r], 0, sizeBoard);
        }
    }

    public Board(int[][] grid) {
        for (int r = 0; r < sizeBoard; r++) {
            System.arraycopy(grid[r], 0, this.grid[r], 0, sizeBoard);
        }
    }

    public int get(int row, int col) {
        validatePositions(row, col);
        return grid[row][col];
    }

    public void set(int row, int col, int cashback) {
        validatePositions(row, col);
        if (cashback < 0) {
            throw new IllegalArgumentException(
                    "Board    |  Cashback не может быть отрицательный, сейчас равен = : " + cashback);
        }
        grid[row][col] = cashback;
    }

    public boolean isEmpty(int row, int col) {
        return get(row, col) == 0;
    }

    public List<Position> getEmptyPositions() {
        List<Position> empty = new ArrayList<>();
        for (int i = 0; i < sizeBoard; i++) {
            for (int j = 0; j < sizeBoard; j++) {
                if (isEmpty(i, j)) {
                    empty.add(new Position(i, j));
                }
            }
        }
        return Collections.unmodifiableList(empty);
    }

    public MoveResult applyMove(Move move) {
        return GameEngine.applyMoveBoardOnly(this, move);
    }

    private void validatePositions(int row, int col) {
        if (row < 0 || row >= constrol || col < 0 || col >= constcol) {
            throw new IndexOutOfBoundsException("Board    |  Invalid position: row=" + row + ", col=" + col);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Board board)) {
            return false;
        }
        for (int r = 0; r < constrol; r++) {
            for (int c = 0; c < constcol; c++) {
                if (this.grid[r][c] != board.grid[r][c]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int res = 1;
        for (int r = 0; r < constrol; r++) {
            for (int c = 0; c < constcol; c++) {
                res = 31 * res + grid[r][c];
            }
        }
        return res;
    }

    @Override
    public String toString() {
        StringBuilder substr = new StringBuilder();
        for (int r = 0; r < constrol; r++) {
            for (int c = 0; c < constcol; c++) {
                int value = grid[r][c];
                if (value == 0) {
                    substr.append(" . ");
                } else {
                    substr.append(String.format("%3d", value));
                }
            }
            substr.append("\n");
        }
        return substr.toString();
    }
}
