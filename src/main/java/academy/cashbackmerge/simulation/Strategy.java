package academy.cashbackmerge.simulation;

import academy.cashbackmerge.engine.GameEngine;
import academy.cashbackmerge.model.GameState;
import academy.cashbackmerge.model.Move;
import academy.cashbackmerge.model.MoveResult;
import java.util.Random;

public final class Strategy {

    private Strategy() {}

    public static Move selectBestMove(GameState state, Random random) {
        Move[] moves = Move.values();
        Move bestMove = null;
        long bestScore = -1;
        int bestMovesCount = 0;

        for (Move move : moves) {
            MoveResult result = GameEngine.applyMoveBoardOnly(state.board(), move);
            if (result.move() && result.score() > bestScore) {
                bestScore = result.score();
                bestMove = move;
                bestMovesCount = 1;
            } else if (result.move() && result.score() == bestScore) {
                if (random.nextInt(++bestMovesCount) == 0) {
                    bestMove = move;
                }
            }
        }

        if (bestMove == null) {
            for (Move move : moves) {
                if (GameEngine.applyMoveBoardOnly(state.board(), move).move()) {
                    return move;
                }
            }
        }

        return bestMove != null ? bestMove : moves[random.nextInt(moves.length)];
    }
}
