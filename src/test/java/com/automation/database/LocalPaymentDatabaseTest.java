package com.automation.database;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;

class LocalPaymentDatabaseTest {

    @Test
    @EnabledIfSystemProperty(named = "localDb", matches = "true")
    void deveConsultarPagamentoNoSqlServerDocker() throws Exception {

        String sql = """
                SELECT TOP 1
                    IdPagamento,
                    IdConta,
                    IdFatura,
                    ValorPagamento,
                    Status
                FROM Pagamentos
                WHERE IdConta = ?
                  AND IdFatura = ?
                ORDER BY IdPagamento DESC
                """;

        try (
                Connection connection = DatabaseConnection.abrir();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, 10001);
            statement.setInt(2, 1);

            try (ResultSet resultado = statement.executeQuery()) {

                assertThat(resultado.next())
                        .as("O pagamento deveria existir no banco")
                        .isTrue();

                int idPagamento = resultado.getInt("IdPagamento");
                int idConta = resultado.getInt("IdConta");
                int idFatura = resultado.getInt("IdFatura");
                BigDecimal valorPagamento =
                        resultado.getBigDecimal("ValorPagamento");
                int status = resultado.getInt("Status");

                assertThat(idPagamento).isPositive();
                assertThat(idConta).isEqualTo(10001);
                assertThat(idFatura).isEqualTo(1);
                assertThat(valorPagamento)
                        .isEqualByComparingTo(new BigDecimal("210.00"));
                assertThat(status).isEqualTo(0);
            }
        }
    }
}