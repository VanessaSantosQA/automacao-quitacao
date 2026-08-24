package com.automation.financialapi;

import java.math.BigDecimal;

public record FaturaRecord(
        int idFatura,
        int idConta,
        BigDecimal valorTotal,
        BigDecimal valorMinimo,
        int status
) {
}