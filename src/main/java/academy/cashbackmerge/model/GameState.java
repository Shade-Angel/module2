package academy.cashbackmerge.model;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.engine.TileSpawn;
import java.util.Random;

public record GameState(Board board, int moveLeft, long score, long seed) {
    public static GameState createInitial(CampaignConfig config, long seed) {
        Random random = new Random(seed);
        Board board = new Board();

        board = TileSpawn.spawnTile(board, config, random);
        board = TileSpawn.spawnTile(board, config, random);
        return new GameState(board, config.movesLimit(), 0L, seed);
    }
}
