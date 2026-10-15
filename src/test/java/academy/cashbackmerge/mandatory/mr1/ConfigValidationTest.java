package academy.cashbackmerge.mandatory.mr1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.ConfigLoader;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: конфиг кампании. */
@DisplayName("MR1. Конфиг кампании")
class ConfigValidationTest {

    @Test
    @DisplayName("Валидный конфиг принимается")
    void validConfigIsAccepted() throws IOException {
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
        assertThat(config.movesLimit()).isEqualTo(10);
        assertThat(config.spawnRules()).hasSize(2);
    }

    @Test
    @DisplayName("Сумма вероятностей spawn-правил не равна 1 — конфиг отклоняется")
    void probabilitiesNotSummingToOneAreRejected() {
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
                        "probability": "0.10"
                    }
                    ]
                }
                """;
        assertThatThrownBy(() -> ConfigLoader.loadFromString(json))
                .hasRootCauseInstanceOf(IllegalArgumentException.class)
                .rootCause()
                .hasMessageContaining("CampaignConfig   |   probability в сумме должны быть равны 1");
    }

    @Test
    @DisplayName("movesLimit <= 0 — конфиг отклоняется")
    void nonPositiveMovesLimitIsRejected() {
        String json = """
                {
                    "movesLimit": 0,
                    "spawnRules": [
                        {
                        "cashback": 1,
                        "probability": "1.0"
                    }
                    ]
                }
                """;
        assertThatThrownBy(() -> ConfigLoader.loadFromString(json))
                .hasRootCauseInstanceOf(IllegalArgumentException.class)
                .rootCause()
                .hasMessageContaining("CampaignConfig   |   movesLimit должен быть больше 0");
    }

    @Test
    @DisplayName("Пустой список spawn-правил — конфиг отклоняется")
    void emptySpawnRulesAreRejected() {
        String json = """
                {
                    "movesLimit": 10,
                    "spawnRules": []
                }
                """;
        assertThatThrownBy(() -> ConfigLoader.loadFromString(json))
                .hasRootCauseInstanceOf(IllegalArgumentException.class)
                .rootCause()
                .hasMessageContaining("CampaignConfig   |  spawnRules не может быть равен null или быть пустым");
    }
}
