package com.automation.database;

import com.automation.repository.LocalPaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.math.BigDecimal;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

class LocalPaymentWriteDatabaseTest {

    @Test
    @EnabledIfSystemProperty(named = "localDb", matches = "true")
    void deveInserirConsultarEValidarPagamento() throws Exception {

        LocalPaymentRepository repository =
                new LocalPaymentRepository();

        try (Connection connection = DatabaseConnection.abrir()) {

            connection.setAutoCommit(false);

            try {

                int idPagamento = repository.inserir(
                        connection,
                        10001,
                        1,
                        new BigDecimal("230.00"),
                        0
                );

                PaymentRecord pagamento = repository
                        .buscarPorId(connection, idPagamento)
                        .orElseThrow(() ->
                                new AssertionError(
                                        "Pagamento nÃ£o encontrado no banco."
                                )
                        );

                assertThat(pagamento.idPagamento())
                        .isPositive();

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
                connection.rollback();
            }
        }
    }
}