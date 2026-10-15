package academy.cashbackmerge.model;

import academy.cashbackmerge.config.CampaignConfig;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

public record GameState(Board board, int moveLeft, long score, long seed) {
    public static GameState createInitial(CampaignConfig config, long seed) {
        Random random = new Random(seed);
        Board board = new Board();

        spawnTile(board, config, random);
        spawnTile(board, config, random);
        return new GameState(board, config.movesLimit(), 0L, seed);
    }

    private static void spawnTile(Board board, CampaignConfig config, Random random) {
        List<Position> emptyPositions = board.getEmptyPositions();
        if (emptyPositions.isEmpty()) {
            // System.out.println("Empty");
            return;
        }
        Position pos = emptyPositions.get(random.nextInt(emptyPositions.size()));
        int cashback = selectCashback(config, random);
        board.set(pos.row(), pos.col(), cashback);
    }

    private static int selectCashback(CampaignConfig config, Random random) {
        long r = random.nextLong() & Long.MAX_VALUE;
        long max = Long.MAX_VALUE;

        long cur = 0;
        for (var rule : config.spawnRules()) {
            long thershold =
                    rule.probability().multiply(BigDecimal.valueOf(max)).longValue();
            cur += thershold;
            if (r < cur) {
                return rule.cashback();
            }
        }
        return config.spawnRules().get(config.spawnRules().size() - 1).cashback();
    }
}
