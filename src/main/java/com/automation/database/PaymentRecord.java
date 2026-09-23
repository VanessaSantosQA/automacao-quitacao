package com.automation.database;

import java.math.BigDecimal;

public record PaymentRecord(
        int idPagamento,
        int idConta,
        int idFatura,
        BigDecimal valorPagamento,
        int status
) {
}