package com.automation.financialapi.config;

public final class ApiConfig {

    private ApiConfig() {
    }

    public static String baseUri() {

        String urlInformada =
                System.getProperty("api.base.url");

        if (urlInformada != null && !urlInformada.isBlank()) {
            return urlInformada;
        }

        String environment =
                System.getProperty(
                        "environment",
                        ConfigReader.get("environment")
                );

        return ConfigReader.get(
                "api.base.url." + environment.toLowerCase()
        );
    }
}