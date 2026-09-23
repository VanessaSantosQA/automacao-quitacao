package com.automation.repository;

import com.automation.database.BoletoQuitacaoRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class LocalBoletoQuitacaoRepository {

    public Optional<BoletoQuitacaoRecord> buscarPorId(
            Connection connection,
            int idBoleto
    ) throws SQLException {

        String sql = """
                SELECT
                    IdBoleto,
                    IdConta,
                    IdFatura,
                    ValorBoleto,
                    Status
                FROM BoletosQuitacao
                WHERE IdBoleto = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, idBoleto);

            try (ResultSet resultado = statement.executeQuery()) {

                if (!resultado.next()) {
                    return Optional.empty();
                }

                return Optional.of(
                        new BoletoQuitacaoRecord(
                                resultado.getInt("IdBoleto"),
                                resultado.getInt("IdConta"),
                                resultado.getInt("IdFatura"),
                                resultado.getBigDecimal("ValorBoleto"),
                                resultado.getInt("Status")
                        )
                );
            }
        }
    }

    public void removerPorId(
            Connection connection,
            int idBoleto
    ) throws SQLException {

        String sql = """
                DELETE FROM BoletosQuitacao
                WHERE IdBoleto = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, idBoleto);
            statement.executeUpdate();
        }
    }
}