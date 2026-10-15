package academy.cashbackmerge.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigLoader {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ConfigLoader() {}

    public static CampaignConfig load(Path path) throws IOException {
        String json = Files.readString(path);
        return MAPPER.readValue(json, CampaignConfig.class);
    }

    public static CampaignConfig loadFromString(String json) throws IOException {
        return MAPPER.readValue(json, CampaignConfig.class);
    }
}
