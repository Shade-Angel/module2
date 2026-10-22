package academy.cashbackmerge.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import org.jspecify.annotations.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CampaignConfig(
        @JsonProperty("movesLimit") int movesLimit,
        @JsonProperty("spawnRules") List<SpawnRule> spawnRules,
        @JsonProperty("rewardTable") @Nullable List<RewardRule> rewardTable,
        @JsonProperty("maxRewardCashback") @Nullable Integer maxRewardCashback,
        @JsonProperty("maxRewardAllowedRate") @Nullable BigDecimal maxRewardAllowedRate) {
    public record SpawnRule(
            @JsonProperty("cashback") int cashback,
            @JsonProperty("probability") BigDecimal probability) {}

    public record RewardRule(
            @JsonProperty("cashback") int cashback,
            @JsonProperty("reward") int reward) {}

    @JsonCreator
    public CampaignConfig {
        if (movesLimit <= 0) {
            throw new IllegalArgumentException(
                    "CampaignConfig   |   movesLimit должен быть больше 0, но сейчас равен = " + movesLimit);
        }
        if (spawnRules == null || spawnRules.isEmpty()) {
            throw new IllegalArgumentException(
                    "CampaignConfig   |  spawnRules не может быть равен null или быть пустым");
        }
        BigDecimal sum = BigDecimal.ZERO;
        for (var rule : spawnRules) {
            if (rule.probability().compareTo(BigDecimal.ZERO) < 0
                    || rule.probability().compareTo(BigDecimal.ONE) > 0) {
                throw new IllegalArgumentException(
                        "CampaignConfig   |  probability может быть в диапозоне от 0 до 1, но сейчас равно = "
                                + rule.probability());
            }
            if (rule.cashback() <= 0) {
                throw new IllegalArgumentException(
                        "CampaignConfig   |   cashback должен быть больше 0, но сейчас равен = " + rule.cashback());
            }
            sum = sum.add(rule.probability());
        }
        if (sum.compareTo(BigDecimal.ONE) != 0) {
            throw new IllegalArgumentException(
                    "CampaignConfig   |   probability в сумме должны быть равны 1, но сейчас равна = " + sum);
        }
    }
}
