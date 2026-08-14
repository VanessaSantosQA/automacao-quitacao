package com.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public final class ConfigReader {

    private static final String AMBIENTE_PADRAO = "test";
    private static final Properties PROPERTIES = new Properties();

    static {
        carregarConfiguracoes();
    }

    private ConfigReader() {
        // Impede instanciação.
    }

    private static void carregarConfiguracoes() {
        String ambiente = obterAmbiente();
        String nomeArquivo = "config-" + ambiente + ".properties";

        try (InputStream input = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream(nomeArquivo)) {

            // O arquivo de configuração é opcional.
            // Caso exista, suas propriedades serão carregadas.
            if (input != null) {
                PROPERTIES.load(input);
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Erro ao carregar o arquivo de configuração: " + nomeArquivo,
                    e
            );
        }
    }

    public static String get(String chave) {
        String valor = buscarValor(chave);

        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException(
                    "Configuração obrigatória não encontrada: " + chave
            );
        }

        return valor.trim();
    }

    public static String getOptional(String chave, String valorPadrao) {
        String valor = buscarValor(chave);

        if (valor == null || valor.isBlank()) {
            return valorPadrao;
        }

        return valor.trim();
    }

    private static String buscarValor(String chave) {

        // 1. Propriedade passada pela JVM:
        // Exemplo: -Ddb.url=...
        String valorJVM = System.getProperty(chave);

        if (valorJVM != null && !valorJVM.isBlank()) {
            return valorJVM;
        }

        // 2. Variável de ambiente:
        // db.url -> DB_URL
        // db.user -> DB_USER
        // db.password -> DB_PASSWORD
        String nomeVariavelAmbiente = converterParaVariavelAmbiente(chave);
        String valorAmbiente = System.getenv(nomeVariavelAmbiente);

        if (valorAmbiente != null && !valorAmbiente.isBlank()) {
            return valorAmbiente;
        }

        // 3. Arquivo config-<ambiente>.properties, caso exista.
        return PROPERTIES.getProperty(chave);
    }

    private static String obterAmbiente() {
        String ambiente = System.getProperty("env");

        if (ambiente == null || ambiente.isBlank()) {
            ambiente = System.getenv("ENV");
        }

        if (ambiente == null || ambiente.isBlank()) {
            ambiente = AMBIENTE_PADRAO;
        }

        return ambiente.trim().toLowerCase(Locale.ROOT);
    }

    private static String converterParaVariavelAmbiente(String chave) {
        return chave
                .toUpperCase(Locale.ROOT)
                .replace(".", "_")
                .replace("-", "_");
    }
}