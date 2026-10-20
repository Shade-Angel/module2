package academy.cashbackmerge.mandatory.mr2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.ConfigLoader;
import academy.cashbackmerge.engine.GameEngine;
import academy.cashbackmerge.model.Board;
import academy.cashbackmerge.model.GameState;
import academy.cashbackmerge.model.Move;
import academy.cashbackmerge.model.MoveResult;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: движок ходов — что считается ходом и что после него происходит. */
@DisplayName("MR2. Движок ходов")
class MoveEngineTest {

    @Test
    @DisplayName("Невалидный ход не меняет поле, не тратит ход и не создаёт плитку")
    void invalidMoveChangesNothing() throws IOException {
        CampaignConfig config = ConfigLoader.loadFromString(
                "{\"movesLimit\": 5, \"spawnRules\": [{\"cashback\": 1, \"probability\": 1.0}]}");
        Board b = new Board();
        b.set(0, 0, 2);
        b.set(0, 1, 4);
        b.set(0, 2, 8);
        b.set(0, 3, 16);
        b.set(1, 0, 32);
        b.set(1, 1, 2);
        b.set(1, 2, 4);
        b.set(1, 3, 8);
        b.set(2, 0, 16);
        b.set(2, 1, 32);
        b.set(2, 2, 2);
        b.set(2, 3, 4);
        b.set(3, 0, 8);
        b.set(3, 1, 16);
        b.set(3, 2, 32);
        b.set(3, 3, 2);
        GameState state = new GameState(b, 5, 0, 42L);

        MoveResult res = GameEngine.applyMove(state, Move.LEFT, config);
        assertFalse(res.move());
        assertEquals(state.board(), res.newBoard());
        assertEquals(state, res.newState());
    }

    @Test
    @DisplayName("После валидного хода появляется ровно одна новая плитка")
    void validMoveSpawnsExactlyOneTile() throws IOException {
        CampaignConfig config = ConfigLoader.loadFromString(
                "{\"movesLimit\": 5, \"spawnRules\": [{\"cashback\": 1, \"probability\": 1.0}]}");
        Board b = new Board();
        b.set(0, 1, 1);
        GameState state = new GameState(b, 5, 0, 42L);

        MoveResult res = GameEngine.applyMove(state, Move.LEFT, config);
        assertTrue(res.move());
        int emptyBefore = state.board().getEmptyPositions().size();
        int emptyAfter = res.newBoard().getEmptyPositions().size();
        assertEquals(emptyBefore - 1, emptyAfter);
    }

    @Test
    @DisplayName("После N валидных ходов игра завершена")
    void gameEndsAfterMovesLimit() throws IOException {
        CampaignConfig config = ConfigLoader.loadFromString(
                "{\"movesLimit\": 2, \"spawnRules\": [{\"cashback\": 1, \"probability\": 1.0}]}");
        Board b = new Board();
        b.set(0, 1, 1);
        b.set(0, 2, 1);
        GameState state = new GameState(b, 2, 0, 42L);

        MoveResult res1 = GameEngine.applyMove(state, Move.LEFT, config);
        assertEquals(1, res1.newState().moveLeft());

        MoveResult res2 = GameEngine.applyMove(res1.newState(), Move.LEFT, config);
        assertEquals(0, res2.newState().moveLeft());
    }

    @Test
    @DisplayName("Достижение плитки 32% не заканчивает игру — доигрываем до лимита ходов")
    void reachingMaxTileDoesNotEndGame() throws IOException {
        CampaignConfig config = ConfigLoader.loadFromString(
                "{\"movesLimit\": 5, \"spawnRules\": [{\"cashback\": 1, \"probability\": 1.0}]}");
        Board b = new Board();
        b.set(0, 0, 16);
        b.set(0, 1, 16);
        b.set(0, 2, 1);
        GameState state = new GameState(b, 5, 0, 42L);

        MoveResult res = GameEngine.applyMove(state, Move.LEFT, config);
        assertTrue(res.merge());
        assertEquals(4, res.newState().moveLeft());

        boolean has32 = false;
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (res.newBoard().get(r, c) == 32) {
                    has32 = true;
                }
            }
        }
        assertTrue(has32);
    }

    @Test
    @DisplayName("Score считается по правилам и растёт только при слияниях")
    void scoreIsCalculatedOnMerges() throws IOException {
        CampaignConfig config = ConfigLoader.loadFromString(
                "{\"movesLimit\": 5, \"spawnRules\": [{\"cashback\": 1, \"probability\": 1.0}]}");
        Board b = new Board();
        b.set(0, 0, 2);
        b.set(0, 1, 2);
        b.set(0, 2, 4);
        b.set(0, 3, 4);
        GameState state = new GameState(b, 5, 10, 42L);

        MoveResult res = GameEngine.applyMove(state, Move.LEFT, config);
        assertEquals(12, res.score());
        assertEquals(22, res.newState().score());
    }
}
