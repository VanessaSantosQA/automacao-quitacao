package com.automation.financialapi;

import java.math.BigDecimal;

public record PagamentoInsertRequest(
        int idConta,
        int idFatura,
        BigDecimal valorPagamento
) {
}