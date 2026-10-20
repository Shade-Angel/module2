package academy.cashbackmerge.model;

import org.jspecify.annotations.Nullable;

public record MoveResult(
        Board board,
        boolean move,
        boolean merge,
        long score,
        @Nullable GameState newState) {
    public Board newBoard() {
        return board;
    }
}
