package academy.cashbackmerge.simulation;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.engine.GameEngine;
import academy.cashbackmerge.model.GameState;
import academy.cashbackmerge.model.Move;
import academy.cashbackmerge.model.MoveResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Console {
    private static final long DELAY_MS = 500; // Задержка между ходами в миллисекундах

    private Console() {}

    public static void runDemo(CampaignConfig config, long seed, String strategy) throws InterruptedException {
        Random random = new Random(seed);
        GameState state = GameState.createInitial(config, seed);
        int step = 1;

        System.out.println("Начало игры Seed: " + seed + ", Стратегия: " + strategy + "\n");

        while (state != null && state.moveLeft() > 0) {
            System.out.println("Ход " + step + " | Очки: " + state.score() + " | Осталось ходов: " + state.moveLeft());
            System.out.println(state.board().toString());

            Move move = pickMoveForDemo(state, strategy, random);
            System.out.println("-> Игрок выбирает направление: " + move);

            MoveResult result = GameEngine.applyMove(state, move, config);
            if (!result.move() || result.newState() == null) {
                System.out.println("Нет доступных ходов. Игра окончена досрочно.");
                break;
            }
            state = result.newState();
            step++;

            Thread.sleep(DELAY_MS);
            System.out.println("--------------------------------------------------");
        }

        System.out.println("ИГРА ОКОНЧЕНА");
        System.out.println("Финальное поле:");
        System.out.println(state.board().toString());
        System.out.println("Итоговый счёт: " + state.score());

        int maxTile = RewardCalculator.getMaxCashback(state.board());
        int reward = RewardCalculator.getReward(maxTile, config);
        System.out.println("Максимальная плитка: " + maxTile + "%");
        System.out.println("Итоговая награда: " + reward);
    }

    private static Move pickMoveForDemo(GameState state, String strategy, Random random) {
        Move[] moves = Move.values();
        List<Move> validMoves = new ArrayList<>();

        for (Move m : moves) {
            if (GameEngine.applyMoveBoardOnly(state.board(), m).move()) {
                validMoves.add(m);
            }
        }

        if (validMoves.isEmpty()) {
            return moves[0];
        }

        if ("greedy-score".equalsIgnoreCase(strategy)) {
            return Strategy.selectBestMove(state, random);
        }

        return validMoves.get(random.nextInt(validMoves.size()));
    }
}
