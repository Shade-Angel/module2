package academy.cashbackmerge.mandatory.mr1;

import static org.assertj.core.api.Assertions.assertThat;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.ConfigLoader;
import academy.cashbackmerge.model.GameState;
import academy.cashbackmerge.simulation.Simulation;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: воспроизводимость по seed. */
@DisplayName("MR1. Воспроизводимость по seed")
class SeedTest {

    @Test
    @DisplayName("Одинаковый конфиг и seed дают одинаковое начальное поле")
    void sameConfigAndSeedProduceSameInitialBoard() throws IOException {
        String json = """
                {
                    "movesLimit": 10,
                    "spawnRules": [
                        {
                            "cashback": 1,
                            "probability": "0.85"
                        },
                        {
                            "cashback": 2,
                            "probability": "0.15"
                        }
                    ]
                }
        """;
        CampaignConfig config = ConfigLoader.loadFromString(json);
        long seed = 42L;

        GameState state1 = GameState.createInitial(config, seed);
        GameState state2 = GameState.createInitial(config, seed);

        assertThat(state1.board()).isEqualTo(state2.board());
        assertThat(state1.moveLeft()).isEqualTo(state2.moveLeft());
        assertThat(state1.score()).isEqualTo(state2.score());
        assertThat(state1.seed()).isEqualTo(state2.seed());
    }

    @Test
    @DisplayName("Одинаковый конфиг, seed и стратегия дают одинаковый отчёт симуляции")
    void sameInputsProduceSameSimulationReport() throws IOException {
        String json = """
                {
                    "movesLimit": 10,
                    "spawnRules": [
                        { "cashback": 1, "probability": "0.85" },
                        { "cashback": 2, "probability": "0.15" }
                    ],
                    "rewardTable": [
                        { "cashback": 1, "reward": 0 },
                        { "cashback": 2, "reward": 100 }
                    ],
                    "maxRewardCashback": 2,
                    "maxRewardAllowedRate": 0.20
                }
        """;
        CampaignConfig config = ConfigLoader.loadFromString(json);
        int players = 100;
        long seed = 42L;
        String strategy = "random";

        Simulation.SimulationResult result1 = Simulation.run(config, players, seed, strategy);
        Simulation.SimulationResult result2 = Simulation.run(config, players, seed, strategy);
        assertThat(result1).isEqualTo(result2);
    }
}
