package com.automation.financialapi;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class BoletoQuitacaoRepository {

    private final JdbcTemplate jdbcTemplate;

    public BoletoQuitacaoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int inserir(
            int idConta,
            int idFatura,
            BigDecimal valorBoleto
    ) {

        String sql = """
                INSERT INTO BoletosQuitacao
                    (IdConta, IdFatura, ValorBoleto, Status)
                VALUES (?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {

                    PreparedStatement statement =
                            connection.prepareStatement(
                                    sql,
                                    Statement.RETURN_GENERATED_KEYS
                            );

                    statement.setInt(1, idConta);
                    statement.setInt(2, idFatura);
                    statement.setBigDecimal(3, valorBoleto);
                    statement.setInt(4, 0);

                    return statement;
                },
                keyHolder
        );

        Number idGerado = keyHolder.getKey();

        if (idGerado == null) {
            throw new IllegalStateException(
                    "SQL Server não retornou o IdBoleto."
            );
        }

        return idGerado.intValue();
    }
}