package academy.cashbackmerge;

import academy.cashbackmerge.config.CampaignConfig;
import academy.cashbackmerge.config.ConfigLoader;
import academy.cashbackmerge.simulation.Console;
import academy.cashbackmerge.simulation.ReportGenerator;
import academy.cashbackmerge.simulation.Simulation;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private Main() {}

    public static void main(String[] args) {
        LOG.debug("Аргументы запуска: ", String.join(" ", args));
        if (args.length == 0) {
            System.out.println(
                    "Использование: simulate --config <path> --players <N> --strategy <strategy> --seed <seed> --output <path>");
            return;
        }

        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 < args.length) {
                params.put(args[i], args[i + 1]);
            }
        }

        if ("demo".equals(args[0]) || params.containsKey("--demo")) {
            try {
                String configPath = params.getOrDefault("--config", "config/campaign.example.json");
                long seed = Long.parseLong(params.getOrDefault("--seed", "42"));
                String strategy = params.getOrDefault("--strategy", "random");

                CampaignConfig config = ConfigLoader.load(Path.of(configPath));
                System.out.println("Загрузка конфига из: " + configPath);
                Console.runDemo(config, seed, strategy);
            } catch (IllegalArgumentException | IOException | InterruptedException e) {
                System.err.println("Ошибка в демо-режиме: " + e.getMessage());
                LOG.error("Demo failed", e);
            }
            return;
        }

        if ("simulate".equals(args[0]) || params.containsKey("--config")) {
            try {
                CampaignConfig config = ConfigLoader.load(Path.of(params.get("--config")));
                int players = Integer.parseInt(params.getOrDefault("--players", "10000"));
                String strategy = params.getOrDefault("--strategy", "random");
                long seed = Long.parseLong(params.getOrDefault("--seed", "42"));
                String outputPath = params.getOrDefault("--output", "report.md");

                Simulation.SimulationResult result = Simulation.run(config, players, seed, strategy);
                ReportGenerator.generate(result, Path.of(outputPath));

                System.out.println("Симуляция завершена. Отчёт: " + outputPath);
                System.out.println("Статус: " + (result.statusOk() ? "OK" : "FAILED"));
            } catch (IllegalArgumentException | IOException e) {
                System.err.println("Ошибка: " + e.getMessage());
                LOG.error("Simulation failed", e);
            }
        } else {
            printUsage();
        }
    }

    private static void printUsage() {
        System.out.println("Использование:");
        System.out.println(
                "  1. Симуляция: simulate --config <path> --players <N> --strategy <random|greedy-score> --seed <N> --output <path>");
        System.out.println("  2. Демонстрация: demo --config <path> --strategy <random|greedy-score> --seed <N>");
    }
}
