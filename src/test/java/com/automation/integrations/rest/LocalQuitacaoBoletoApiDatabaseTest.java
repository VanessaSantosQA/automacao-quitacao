package com.automation.integrations.rest;

import com.automation.database.BoletoQuitacaoRecord;
import com.automation.database.DatabaseConnection;
import com.automation.repository.LocalBoletoQuitacaoRepository;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.math.BigDecimal;
import java.sql.Connection;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class LocalQuitacaoBoletoApiDatabaseTest {

    private static final int ID_CONTA = 10001;

    @Test
    @EnabledIfSystemProperty(named = "localApi", matches = "true")
    void deveGerarBoletoEValidarNoBanco() throws Exception {

        Response simulacao =
                given()
                        .baseUri("http://localhost:8081")
                        .contentType("application/json")
                        .body("""
                                {
                                  "idConta": 10001
                                }
                                """)
                .when()
                        .post("/quitacao/simular")
                .then()
                        .statusCode(200)
                        .extract()
                        .response();

        int idFatura =
                simulacao.jsonPath()
                        .getInt("idFatura");

        BigDecimal valorTotal =
                new BigDecimal(
                        simulacao.jsonPath()
                                .getString("valorTotal")
                );

        Response boletoResponse =
                given()
                        .baseUri("http://localhost:8081")
                        .contentType("application/json")
                        .body("""
                                {
                                  "idConta": 10001
                                }
                                """)
                .when()
                        .post("/quitacao/gerar-boleto")
                .then()
                        .statusCode(201)
                        .extract()
                        .response();

        int idBoleto =
                boletoResponse.jsonPath()
                        .getInt("idBoleto");

        BigDecimal valorBoletoApi =
                new BigDecimal(
                        boletoResponse.jsonPath()
                                .getString("valorBoleto")
                );

        assertThat(idBoleto)
                .isPositive();

        assertThat(valorBoletoApi)
                .as("Valor do boleto deve ser igual ao valor total simulado")
                .isEqualByComparingTo(valorTotal);

        LocalBoletoQuitacaoRepository repository =
                new LocalBoletoQuitacaoRepository();

        try (Connection connection =
                     DatabaseConnection.abrir()) {

            try {

                BoletoQuitacaoRecord boleto = repository
                        .buscarPorId(connection, idBoleto)
                        .orElseThrow(() ->
                                new AssertionError(
                                        "Boleto criado pela API nÃ£o foi encontrado no banco."
                                )
                        );

                assertThat(boleto.idBoleto())
                        .isEqualTo(idBoleto);

                assertThat(boleto.idConta())
                        .isEqualTo(ID_CONTA);

                assertThat(boleto.idFatura())
                        .isEqualTo(idFatura);

                assertThat(boleto.valorBoleto())
                        .as("Valor persistido deve ser igual ao valor total simulado")
                        .isEqualByComparingTo(valorTotal);

                assertThat(boleto.status())
                        .isEqualTo(0);

            } finally {

                repository.removerPorId(
                        connection,
                        idBoleto
                );
            }
        }
    }
}