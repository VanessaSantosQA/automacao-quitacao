package com.automation.integrations.rest;

import com.automation.integrations.rest.config.ApiConfig;

import com.automation.business.PaymentAmountCalculator;
import com.automation.database.DatabaseConnection;
import com.automation.database.PaymentRecord;
import com.automation.repository.LocalPaymentRepository;
import io.restassured.response.Response;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.sql.Connection;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class LocalPaymentScenariosApiTest {

    private static final int ID_CONTA = 10001;

    enum CenarioPagamento {
        MENOR_QUE_MINIMO,
        MINIMO,
        PARCIAL_20,
        PARCIAL_60,
        TOTAL,
        MAIOR_QUE_TOTAL
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(CenarioPagamento.class)
    @EnabledIfSystemProperty(named = "localApi", matches = "true")
    void deveCalcularCriarEValidarPagamento(
            CenarioPagamento cenario
    ) throws Exception {

        Response simulacao =
                given()
                        .baseUri(ApiConfig.localBaseUrl())
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
                simulacao.jsonPath().getInt("idFatura");

        BigDecimal valorMinimo =
                new BigDecimal(
                        simulacao.jsonPath().getString("valorMinimo")
                );

        BigDecimal valorTotal =
                new BigDecimal(
                        simulacao.jsonPath().getString("valorTotal")
                );

        BigDecimal valorPagamento =
                calcularValorComRegraDeNegocio(
                        cenario,
                        valorMinimo,
                        valorTotal
                );

        String bodyPagamento = """
                {
                  "idConta": %d,
                  "idFatura": %d,
                  "valorPagamento": %s
                }
                """.formatted(
                        ID_CONTA,
                        idFatura,
                        valorPagamento.toPlainString()
                );

        Response pagamentoResponse =
                given()
                        .baseUri(ApiConfig.localBaseUrl())
                        .contentType("application/json")
                        .body(bodyPagamento)
                .when()
                        .post("/pagamentos")
                .then()
                        .statusCode(201)
                        .extract()
                        .response();

        int idPagamento =
                pagamentoResponse
                        .jsonPath()
                        .getInt("idPagamento");

        LocalPaymentRepository repository =
                new LocalPaymentRepository();

        try (Connection connection =
                     DatabaseConnection.abrir()) {

            try {

                PaymentRecord pagamento = repository
                        .buscarPorId(connection, idPagamento)
                        .orElseThrow(() ->
                                new AssertionError(
                                        "Pagamento nÃ£o encontrado para o cenÃ¡rio "
                                                + cenario
                                )
                        );

                assertThat(pagamento.idConta())
                        .isEqualTo(ID_CONTA);

                assertThat(pagamento.idFatura())
                        .isEqualTo(idFatura);

                assertThat(pagamento.valorPagamento())
                        .as("Valor do cenÃ¡rio " + cenario)
                        .isEqualByComparingTo(valorPagamento);

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

    private BigDecimal calcularValorComRegraDeNegocio(
            CenarioPagamento cenario,
            BigDecimal valorMinimo,
            BigDecimal valorTotal
    ) {

        return switch (cenario) {

            case MENOR_QUE_MINIMO ->
                    PaymentAmountCalculator.menorQueMinimo(
                            valorMinimo
                    );

            case MINIMO ->
                    PaymentAmountCalculator.pagamentoMinimo(
                            valorMinimo
                    );

            case PARCIAL_20 ->
                    PaymentAmountCalculator.parcial(
                            valorMinimo,
                            valorTotal,
                            new BigDecimal("0.20")
                    );

            case PARCIAL_60 ->
                    PaymentAmountCalculator.parcial(
                            valorMinimo,
                            valorTotal,
                            new BigDecimal("0.60")
                    );

            case TOTAL ->
                    PaymentAmountCalculator.pagamentoTotal(
                            valorTotal
                    );

            case MAIOR_QUE_TOTAL ->
                    PaymentAmountCalculator.maiorQueTotal(
                            valorTotal
                    );
        };
    }
}