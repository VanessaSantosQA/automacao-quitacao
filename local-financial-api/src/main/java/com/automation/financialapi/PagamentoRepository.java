package com.automation.financialapi;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class PagamentoRepository {

    private final JdbcTemplate jdbcTemplate;

    public PagamentoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int inserir(PagamentoInsertRequest request) {

        String sql = """
                INSERT INTO Pagamentos
                    (IdConta, IdFatura, ValorPagamento, Status)
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

                    statement.setInt(1, request.idConta());
                    statement.setInt(2, request.idFatura());
                    statement.setBigDecimal(
                            3,
                            request.valorPagamento()
                    );
                    statement.setInt(4, 0);

                    return statement;
                },
                keyHolder
        );

        Number idGerado = keyHolder.getKey();

        if (idGerado == null) {
            throw new IllegalStateException(
                    "SQL Server nÃ£o retornou o IdPagamento."
            );
        }

        return idGerado.intValue();
    }
}