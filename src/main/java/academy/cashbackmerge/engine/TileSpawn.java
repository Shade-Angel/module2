package academy.cashbackmerge.engine;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.model.Board;
import academy.cashbackmerge.model.Position;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

public final class TileSpawn {

    private TileSpawn() {}

    public static Board spawnTile(Board board, CampaignConfig config, Random random) {
        List<Position> emptyPositions = board.getEmptyPositions();
        if (emptyPositions.isEmpty()) {
            return board;
        }

        Position pos = emptyPositions.get(random.nextInt(emptyPositions.size()));
        int cashback = selectCashback(config, random);

        Board newBoard = new Board(board);
        newBoard.set(pos.row(), pos.col(), cashback);
        return newBoard;
    }

    private static int selectCashback(CampaignConfig config, Random random) {
        long r = random.nextLong() & Long.MAX_VALUE;
        long max = Long.MAX_VALUE;
        long cur = 0;

        for (var rule : config.spawnRules()) {
            long threshold =
                    rule.probability().multiply(BigDecimal.valueOf(max)).longValue();
            cur += threshold;
            if (r < cur) {
                return rule.cashback();
            }
        }

        return config.spawnRules().get(config.spawnRules().size() - 1).cashback();
    }
}
