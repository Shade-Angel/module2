package academy.cashbackmerge;

import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.cashbackmerge.simulation.ReportGenerator;
import academy.cashbackmerge.simulation.Simulation.SimulationResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("MR3. Генерация отчета")
class ReportTest {

    @Test
    @DisplayName("Отчет успешно создается даже в несуществующей вложенной папке")
    void reportIsGeneratedInNestedDirectory(@TempDir Path tempDir) throws Exception {
        SimulationResult mockResult = new SimulationResult(
                100, 50, 42L, "random", Map.of(16, 80, 32, 20), Map.of(500, 80, 2000, 20), 1500.5, 5000, 0.20, true);

        Path outputPath = tempDir.resolve("reports").resolve("nested").resolve("test_report.md");

        ReportGenerator.generate(mockResult, outputPath);

        assertTrue(Files.exists(outputPath), "Файл отчета должен быть создан");

        String content = Files.readString(outputPath);

        assertTrue(
                content.contains("**Статус**: OK"),
                "Отчет должен содержать статус OK. Реальное содержимое:\n" + content);
        assertTrue(content.contains("16%"), "Отчет должен содержать распределение плиток");
    }
}
