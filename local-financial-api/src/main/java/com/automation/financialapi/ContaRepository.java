package com.automation.financialapi;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ContaRepository {

    private final JdbcTemplate jdbcTemplate;

    public ContaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<ContaRecord> buscarPorId(int idConta) {

        String sql = """
                SELECT
                    IdConta,
                    Nome,
                    SaldoAtual,
                    Status
                FROM Contas
                WHERE IdConta = ?
                """;

        return jdbcTemplate.query(
                sql,
                rs -> {
                    if (!rs.next()) {
                        return Optional.empty();
                    }

                    return Optional.of(
                            new ContaRecord(
                                    rs.getInt("IdConta"),
                                    rs.getString("Nome"),
                                    rs.getBigDecimal("SaldoAtual"),
                                    rs.getInt("Status")
                            )
                    );
                },
                idConta
        );
    }
}