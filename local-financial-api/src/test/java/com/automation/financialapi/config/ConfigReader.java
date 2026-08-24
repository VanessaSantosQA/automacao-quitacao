package com.automation.financialapi.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input =
                     ConfigReader.class
                             .getClassLoader()
                             .getResourceAsStream("api.properties")) {

            if (input == null) {
                throw new IllegalStateException(
                        "Arquivo api.properties não encontrado"
                );
            }

            PROPERTIES.load(input);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Erro ao carregar api.properties",
                    e
            );
        }
    }

    private ConfigReader() {
    }

    public static String get(String key) {

        String value = PROPERTIES.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Configuração não encontrada: " + key
            );
        }

        return value;
    }
}