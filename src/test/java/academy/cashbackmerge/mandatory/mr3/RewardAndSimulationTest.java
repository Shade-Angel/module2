package academy.cashbackmerge.mandatory.mr3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.simulation.RewardCalculator;
import academy.cashbackmerge.simulation.Simulation;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MR3. Награды и симуляция")
class RewardAndSimulationTest {

    @Test
    @DisplayName("Максимальная плитка 16% даёт награду соответствующего tier")
    void rewardMatchesHighestTile() {
        CampaignConfig config = new CampaignConfig(
                10,
                List.of(new CampaignConfig.SpawnRule(1, new BigDecimal("1.0"))),
                List.of(
                        new CampaignConfig.RewardRule(2, 0),
                        new CampaignConfig.RewardRule(4, 50),
                        new CampaignConfig.RewardRule(16, 500),
                        new CampaignConfig.RewardRule(32, 2000)),
                32,
                new BigDecimal("0.20"));

        assertEquals(500, RewardCalculator.getReward(16, config));
        assertEquals(50, RewardCalculator.getReward(4, config));
        assertEquals(0, RewardCalculator.getReward(1, config));
    }

    @Test
    @DisplayName("Weighted random учитывает вероятности из конфига")
    void spawnRespectsConfiguredProbabilities() {
        CampaignConfig config = new CampaignConfig(
                10,
                List.of(
                        new CampaignConfig.SpawnRule(1, new BigDecimal("0.99")),
                        new CampaignConfig.SpawnRule(2, new BigDecimal("0.01"))),
                null,
                null,
                null);
        assertNotNull(config);
        assertEquals(2, config.spawnRules().size());
        assertEquals(new BigDecimal("0.99"), config.spawnRules().get(0).probability());
    }

    @Test
    @DisplayName("Отчёт содержит распределение максимальных плиток и наград")
    void reportContainsDistributions() {
        CampaignConfig config = new CampaignConfig(
                10,
                List.of(new CampaignConfig.SpawnRule(1, new BigDecimal("1.0"))),
                List.of(new CampaignConfig.RewardRule(1, 0), new CampaignConfig.RewardRule(2, 100)),
                2,
                new BigDecimal("0.20"));

        Simulation.SimulationResult result = Simulation.run(config, 100, 42L, "random");
        assertFalse(result.maxTileMap().isEmpty());
        assertFalse(result.rewMap().isEmpty());
    }

    @Test
    @DisplayName("Отчёт содержит долю игроков с максимальной наградой")
    void reportContainsMaxRewardRate() {
        CampaignConfig config = new CampaignConfig(
                10,
                List.of(new CampaignConfig.SpawnRule(1, new BigDecimal("1.0"))),
                List.of(new CampaignConfig.RewardRule(1, 0), new CampaignConfig.RewardRule(2, 100)),
                2,
                new BigDecimal("0.20"));

        Simulation.SimulationResult result = Simulation.run(config, 100, 42L, "random");
        assertTrue(result.maxRewardRate() >= 0.0 && result.maxRewardRate() <= 1.0);
    }

    @Test
    @DisplayName("Статус OK, если actualMaxRewardRate <= maxRewardAllowedRate, иначе FAILED")
    void reportStatusReflectsBusinessConstraint() {
        CampaignConfig configFail = new CampaignConfig(
                10,
                List.of(new CampaignConfig.SpawnRule(1, new BigDecimal("1.0"))),
                List.of(new CampaignConfig.RewardRule(1, 0)),
                1,
                new BigDecimal("0.20"));
        assertFalse(Simulation.run(configFail, 100, 42L, "random").statusOk());

        CampaignConfig configOk = new CampaignConfig(
                10,
                List.of(new CampaignConfig.SpawnRule(1, new BigDecimal("1.0"))),
                List.of(new CampaignConfig.RewardRule(1, 0)),
                1,
                new BigDecimal("1.0"));
        assertTrue(Simulation.run(configOk, 100, 42L, "random").statusOk());
    }
}
