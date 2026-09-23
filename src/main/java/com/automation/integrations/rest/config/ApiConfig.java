package com.automation.integrations.rest.config;

import com.automation.config.ConfigReader;

public final class ApiConfig {

    private static final String DEFAULT_PUBLIC_BASE_URL =
            "https://jsonplaceholder.typicode.com";

    private static final String DEFAULT_LOCAL_BASE_URL =
            "http://localhost:8081";

    private ApiConfig() {
    }

    public static String baseUrl() {
        return ConfigReader.getOptional(
                "api.base.url",
                DEFAULT_PUBLIC_BASE_URL
        );
    }

    public static String localBaseUrl() {
        return ConfigReader.getOptional(
                "api.local.base.url",
                DEFAULT_LOCAL_BASE_URL
        );
    }
}