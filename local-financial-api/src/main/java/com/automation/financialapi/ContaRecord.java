package com.automation.financialapi;

import java.math.BigDecimal;

public record ContaRecord(
        int idConta,
        String nome,
        BigDecimal saldoAtual,
        int status
) {
}