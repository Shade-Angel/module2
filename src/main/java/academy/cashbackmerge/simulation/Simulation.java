package academy.cashbackmerge.simulation;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.engine.GameEngine;
import academy.cashbackmerge.model.GameState;
import academy.cashbackmerge.model.Move;
import academy.cashbackmerge.model.MoveResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public final class Simulation {
    private static final int MAX_CASHBACK = 32;
    private static final double RATE = 0.20;

    private Simulation() {}

    public record SimulationResult(
            int players,
            int movesLimit,
            long seed,
            String strategy,
            Map<Integer, Integer> maxTileMap,
            Map<Integer, Integer> rewMap,
            double avergeScore,
            long maxScore,
            double maxRewardRate,
            boolean statusOk) {}

    public static SimulationResult run(CampaignConfig config, int players, long seed, String strategy) {
        Random random = new Random(seed);
        Map<Integer, Integer> maxTileDist = new TreeMap<>();
        Map<Integer, Integer> rewardDist = new TreeMap<>();
        long totalScore = 0;
        long maxScore = 0;
        int maxRewardCount = 0;

        int maxRewardValue = getMaxRewardValue(config);

        for (int i = 0; i < players; i++) {
            GameState state = GameState.createInitial(config, seed + i);
            while (state != null && state.moveLeft() > 0) {
                Move move = selectMove(state, strategy, random);
                MoveResult res = GameEngine.applyMove(state, move, config);

                if (!res.move() || res.newState() == null) {
                    break;
                }
                state = res.newState();
            }

            int maxTile = RewardCalculator.getMaxCashback(state.board());
            int reward = RewardCalculator.getReward(maxTile, config);
            maxTileDist.merge(maxTile, 1, Integer::sum);
            rewardDist.merge(reward, 1, Integer::sum);

            totalScore += state.score();
            if (state.score() > maxScore) {
                maxScore = state.score();
            }
            if (reward == maxRewardValue) {
                maxRewardCount++;
            }
        }

        double avgScore = players > 0 ? (double) totalScore / players : 0.0;
        double actualMaxRate = players > 0 ? (double) maxRewardCount / players : 0.0;
        double allowedRate = config.maxRewardAllowedRate() != null
                ? config.maxRewardAllowedRate().doubleValue()
                : RATE;
        boolean statusOk = actualMaxRate <= allowedRate;

        return new SimulationResult(
                players,
                config.movesLimit(),
                seed,
                strategy,
                maxTileDist,
                rewardDist,
                avgScore,
                maxScore,
                actualMaxRate,
                statusOk);
    }

    private static int getMaxRewardValue(CampaignConfig config) {
        int maxPossibleCashback = config.maxRewardCashback() != null ? config.maxRewardCashback() : MAX_CASHBACK;
        int maxRewardValue = 0;
        if (config.rewardTable() != null) {
            for (var rule : config.rewardTable()) {
                if (rule.cashback() == maxPossibleCashback) {
                    maxRewardValue = rule.reward();
                }
            }
        }
        return maxRewardValue;
    }

    private static Move selectMove(GameState state, String strategy, Random random) {
        if ("random".equalsIgnoreCase(strategy)) {
            Move[] moves = Move.values();
            List<Move> validMoves = new ArrayList<>();
            for (Move m : moves) {
                if (GameEngine.applyMoveBoardOnly(state.board(), m).move()) {
                    validMoves.add(m);
                }
            }
            return validMoves.isEmpty()
                    ? moves[random.nextInt(moves.length)]
                    : validMoves.get(random.nextInt(validMoves.size()));
        } else if ("greedy-score".equalsIgnoreCase(strategy)) {
            return Strategy.selectBestMove(state, random);
        }
        throw new IllegalArgumentException("Simulation   |  неизвестная стратегия: " + strategy);
    }
}
