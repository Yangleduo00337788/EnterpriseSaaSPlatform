package com.flowcloud.admin.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads .env into Spring Environment before bean creation.
 * Real OS environment variables still keep higher precedence.
 */
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY_SOURCE_NAME = "flowcloudDotenv";
    private static final List<String> DOTENV_FILE_NAMES = List.of("application.env", ".env");

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Path dotenvPath = resolveDotenvPath();
        if (dotenvPath == null || !Files.isRegularFile(dotenvPath)) {
            return;
        }

        Map<String, Object> properties = loadDotenv(dotenvPath);
        if (properties.isEmpty()) {
            return;
        }

        MutablePropertySources propertySources = environment.getPropertySources();
        MapPropertySource propertySource = new MapPropertySource(PROPERTY_SOURCE_NAME, properties);
        if (propertySources.contains(PROPERTY_SOURCE_NAME)) {
            propertySources.replace(PROPERTY_SOURCE_NAME, propertySource);
            return;
        }
        if (propertySources.contains(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME)) {
            propertySources.addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, propertySource);
            return;
        }
        propertySources.addLast(propertySource);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private Path resolveDotenvPath() {
        String userDir = System.getProperty("user.dir");
        if (!StringUtils.hasText(userDir)) {
            return null;
        }

        Path currentDir = Path.of(userDir).toAbsolutePath().normalize();
        for (String fileName : DOTENV_FILE_NAMES) {
            List<Path> candidates = List.of(
                    currentDir.resolve(fileName),
                    currentDir.getParent() != null ? currentDir.getParent().resolve(fileName) : currentDir.resolveSibling(fileName)
            );
            for (Path candidate : candidates) {
                if (Files.isRegularFile(candidate)) {
                    return candidate;
                }
            }
        }
        return null;
    }

    private Map<String, Object> loadDotenv(Path dotenvPath) {
        try {
            List<String> lines = Files.readAllLines(dotenvPath, StandardCharsets.UTF_8);
            Map<String, Object> properties = new LinkedHashMap<>();
            for (String rawLine : lines) {
                parseLine(rawLine, properties);
            }
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException(".env 文件读取失败: " + dotenvPath, e);
        }
    }

    private void parseLine(String rawLine, Map<String, Object> properties) {
        if (rawLine == null) {
            return;
        }
        String line = stripBom(rawLine).trim();
        if (!StringUtils.hasText(line) || line.startsWith("#")) {
            return;
        }
        if (line.startsWith("export ")) {
            line = line.substring(7).trim();
        }

        int separatorIndex = line.indexOf('=');
        if (separatorIndex <= 0) {
            return;
        }

        String key = line.substring(0, separatorIndex).trim();
        String value = line.substring(separatorIndex + 1).trim();
        if (!StringUtils.hasText(key)) {
            return;
        }

        properties.put(key, unwrapQuotedValue(value));
    }

    private String unwrapQuotedValue(String value) {
        if (value.length() >= 2) {
            if ((value.startsWith("\"") && value.endsWith("\""))
                    || (value.startsWith("'") && value.endsWith("'"))) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }

    private String stripBom(String value) {
        if (!StringUtils.hasLength(value)) {
            return value;
        }
        return value.charAt(0) == '\uFEFF' ? value.substring(1) : value;
    }
}
