package academy.cashbackmerge.simulation;

import academy.cashbackmerge.simulation.Simulation.SimulationResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;

public final class ReportGenerator {
    private static final int HUNGRED = 100;

    private ReportGenerator() {}

    public static void generate(SimulationResult result, Path outputPath) throws IOException {
        DecimalFormat df = new DecimalFormat("#.####");
        StringBuilder sb = new StringBuilder();

        sb.append("# Отчёт симуляции Cashback Merge\n\n");
        sb.append("- **Игроков**: ").append(result.players()).append("\n");
        sb.append("- **Лимит ходов**: ").append(result.movesLimit()).append("\n");
        sb.append("- **Seed**: ").append(result.seed()).append("\n");
        sb.append("- **Стратегия**: ").append(result.strategy()).append("\n\n");

        sb.append("## Распределение максимальных плиток\n");
        for (var entry : result.maxTileMap().entrySet()) {
            sb.append("- ")
                    .append(entry.getKey())
                    .append("%: ")
                    .append(entry.getValue())
                    .append(" игроков\n");
        }

        sb.append("\n## Распределение наград\n");
        for (var entry : result.rewMap().entrySet()) {
            sb.append("- Награда ")
                    .append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(" игроков\n");
        }

        sb.append("\n## Статистика\n");
        sb.append("- **Средний счёт**: ")
                .append(df.format(result.avergeScore()))
                .append("\n");
        sb.append("- **Максимальный счёт**: ").append(result.maxScore()).append("\n");
        sb.append("- **Доля игроков с максимальной наградой**: ")
                .append(df.format(result.maxRewardRate() * HUNGRED))
                .append("%\n");

        sb.append("- **Статус**: ").append(result.statusOk() ? "OK" : "FAILED").append("\n");

        if (outputPath.getParent() != null) {
            Files.createDirectories(outputPath.getParent());
        }
        Files.writeString(outputPath, sb.toString());
    }
}
