package academy.cashbackmerge.simulation;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.model.Board;

public final class RewardCalculator {
    private static final int SIZE = 4;

    private RewardCalculator() {}

    public static int getMaxCashback(Board board) {
        int max = 0;
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                int val = board.get(r, c);
                if (val > max) {
                    max = val;
                }
            }
        }
        return max;
    }

    public static int getReward(int maxCashback, CampaignConfig config) {
        if (config.rewardTable() == null) {
            return 0;
        }
        int bestReward = 0;
        for (var rule : config.rewardTable()) {
            if (maxCashback >= rule.cashback()) {
                if (rule.reward() > bestReward) {
                    bestReward = rule.reward();
                }
            }
        }
        return bestReward;
    }
}
