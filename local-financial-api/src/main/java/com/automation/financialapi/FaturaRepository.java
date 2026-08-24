package com.automation.financialapi;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class FaturaRepository {

    private final JdbcTemplate jdbcTemplate;

    public FaturaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<FaturaRecord> buscarPorIdConta(int idConta) {

        String sql = """
                SELECT TOP 1
                    IdFatura,
                    IdConta,
                    ValorTotal,
                    ValorMinimo,
                    Status
                FROM Faturas
                WHERE IdConta = ?
                ORDER BY IdFatura DESC
                """;

        return jdbcTemplate.query(
                sql,
                rs -> {
                    if (!rs.next()) {
                        return Optional.empty();
                    }

                    return Optional.of(
                            new FaturaRecord(
                                    rs.getInt("IdFatura"),
                                    rs.getInt("IdConta"),
                                    rs.getBigDecimal("ValorTotal"),
                                    rs.getBigDecimal("ValorMinimo"),
                                    rs.getInt("Status")
                            )
                    );
                },
                idConta
        );
    }
}