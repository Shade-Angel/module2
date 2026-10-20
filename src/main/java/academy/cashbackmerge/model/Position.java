package academy.cashbackmerge.model;

public record Position(int row, int col) {
    private static final int SIZE = 4;

    public Position {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            throw new IllegalArgumentException(
                    "Position   |   Позиция должна быть от 0 до 3, но row = " + row + "  и col = " + col);
        }
    }
}
