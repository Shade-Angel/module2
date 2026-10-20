package academy.cashbackmerge.engine;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.model.Board;
import academy.cashbackmerge.model.GameState;
import academy.cashbackmerge.model.Move;
import academy.cashbackmerge.model.MoveResult;
import java.util.Random;

public final class GameEngine {

    private static final int SIZE = 4;

    private GameEngine() {}

    public static MoveResult applyMove(GameState state, Move move, CampaignConfig config) {
        MoveResult boardRes = applyMoveBoard(state.board(), move);

        if (!boardRes.move()) {
            return new MoveResult(state.board(), false, false, 0, state);
        }

        long newSeed = state.seed() + 1;
        Random random = new Random(newSeed);
        Board spawnBoard = TileSpawn.spawnTile(boardRes.board(), config, random);

        GameState newState = new GameState(spawnBoard, state.moveLeft() - 1, state.score() + boardRes.score(), newSeed);

        return new MoveResult(spawnBoard, true, boardRes.merge(), boardRes.score(), newState);
    }

    public static MoveResult applyMoveBoardOnly(Board board, Move move) {
        return applyMoveBoard(board, move);
    }

    private static MoveResult applyMoveBoard(Board board, Move move) {
        int[][] newGrid = new int[SIZE][SIZE];
        boolean moved = false;
        boolean merged = false;
        long score = 0;

        for (int i = 0; i < SIZE; i++) {
            int[] line = extractLine(board, move, i);

            if (move == Move.RIGHT || move == Move.DOWN) {
                line = LineProcess.reverse(line);
            }

            LineProcess.LineResult res = LineProcess.process(line);
            score += res.scoreAdded();
            if (res.moved()) {
                moved = true;
            }
            if (res.merged()) {
                merged = true;
            }

            int[] finalLine = res.line().values();
            if (move == Move.RIGHT || move == Move.DOWN) {
                finalLine = LineProcess.reverse(finalLine);
            }

            applyLine(newGrid, move, i, finalLine);
        }
        Board resBoard = new Board(newGrid);
        return new MoveResult(resBoard, moved, merged, score, null);
    }

    private static int[] extractLine(Board board, Move move, int index) {
        int[] line = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            line[i] = switch (move) {
                case LEFT, RIGHT -> board.get(index, i);
                case UP, DOWN -> board.get(i, index);
            };
        }
        return line;
    }

    private static void applyLine(int[][] grid, Move move, int index, int[] line) {
        for (int i = 0; i < SIZE; i++) {
            switch (move) {
                case LEFT, RIGHT -> grid[index][i] = line[i];
                case UP, DOWN -> grid[i][index] = line[i];
            }
        }
    }
}
