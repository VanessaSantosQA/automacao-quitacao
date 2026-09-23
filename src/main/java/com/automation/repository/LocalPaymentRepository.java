package com.automation.repository;

import com.automation.database.PaymentRecord;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class LocalPaymentRepository {

    public int inserir(
            Connection connection,
            int idConta,
            int idFatura,
            BigDecimal valorPagamento,
            int status
    ) throws SQLException {

        String sql = """
                INSERT INTO Pagamentos
                    (IdConta, IdFatura, ValorPagamento, Status)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
        )) {

            statement.setInt(1, idConta);
            statement.setInt(2, idFatura);
            statement.setBigDecimal(3, valorPagamento);
            statement.setInt(4, status);

            int linhasInseridas = statement.executeUpdate();

            if (linhasInseridas != 1) {
                throw new SQLException(
                        "Quantidade inesperada de registros inseridos: "
                                + linhasInseridas
                );
            }

            try (ResultSet chaves = statement.getGeneratedKeys()) {

                if (!chaves.next()) {
                    throw new SQLException(
                            "SQL Server nÃ£o retornou o IdPagamento."
                    );
                }

                return chaves.getInt(1);
            }
        }
    }

    public Optional<PaymentRecord> buscarPorId(
            Connection connection,
            int idPagamento
    ) throws SQLException {

        String sql = """
                SELECT
                    IdPagamento,
                    IdConta,
                    IdFatura,
                    ValorPagamento,
                    Status
                FROM Pagamentos
                WHERE IdPagamento = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, idPagamento);

            try (ResultSet resultado = statement.executeQuery()) {

                if (!resultado.next()) {
                    return Optional.empty();
                }

                return Optional.of(
                        new PaymentRecord(
                                resultado.getInt("IdPagamento"),
                                resultado.getInt("IdConta"),
                                resultado.getInt("IdFatura"),
                                resultado.getBigDecimal("ValorPagamento"),
                                resultado.getInt("Status")
                        )
                );
            }
        }
    }

    public void removerPorId(
            Connection connection,
            int idPagamento
    ) throws SQLException {

        String sql = """
                DELETE FROM Pagamentos
                WHERE IdPagamento = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, idPagamento);
            statement.executeUpdate();
        }
    }
}