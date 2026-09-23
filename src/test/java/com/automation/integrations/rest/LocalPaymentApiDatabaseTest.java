package com.automation.integrations.rest;

import com.automation.integrations.rest.config.ApiConfig;

import com.automation.database.DatabaseConnection;
import com.automation.database.PaymentRecord;
import com.automation.repository.LocalPaymentRepository;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.math.BigDecimal;
import java.sql.Connection;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class LocalPaymentApiDatabaseTest {

    @Test
    @EnabledIfSystemProperty(named = "localApi", matches = "true")
    void deveCriarPagamentoPelaApiEValidarNoBanco() throws Exception {

        String body = """
                {
                  "idConta": 10001,
                  "idFatura": 1,
                  "valorPagamento": 230.00
                }
                """;

        Response response =
                given()
                        .baseUri(ApiConfig.localBaseUrl())
                        .contentType("application/json")
                        .body(body)

                .when()
                        .post("/pagamentos")

                .then()
                        .statusCode(201)
                        .extract()
                        .response();

        int idPagamento =
                response.jsonPath().getInt("idPagamento");

        LocalPaymentRepository repository =
                new LocalPaymentRepository();

        try (Connection connection = DatabaseConnection.abrir()) {

            try {

                PaymentRecord pagamento = repository
                        .buscarPorId(connection, idPagamento)
                        .orElseThrow(() ->
                                new AssertionError(
                                        "Pagamento criado pela API nÃ£o foi encontrado no banco."
                                )
                        );

                assertThat(pagamento.idPagamento())
                        .isEqualTo(idPagamento);

                assertThat(pagamento.idConta())
                        .isEqualTo(10001);

                assertThat(pagamento.idFatura())
                        .isEqualTo(1);

                assertThat(pagamento.valorPagamento())
                        .isEqualByComparingTo(
                                new BigDecimal("230.00")
                        );

                assertThat(pagamento.status())
                        .isEqualTo(0);

            } finally {
                repository.removerPorId(
                        connection,
                        idPagamento
                );
            }
        }
    }
}