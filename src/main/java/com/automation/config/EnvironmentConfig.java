package com.automation.config;

public final class EnvironmentConfig {

    private EnvironmentConfig() {
    }

    public static String getEnvironment() {

        String environment = System.getProperty("env");

        if (environment == null || environment.isBlank()) {
            environment = System.getenv("ENV");
        }

        if (environment == null || environment.isBlank()) {
            environment = ConfigReader.get("environment");
        }

        return environment.trim().toLowerCase();
    }

    public static String getBaseUrl() {

        String environment = getEnvironment();

        try {
            return ConfigReader.get("base.url." + environment);
        } catch (IllegalStateException e) {
            return ConfigReader.get("base.url");
        }
    }
}
