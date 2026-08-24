package com.automation.financialapi;

import java.math.BigDecimal;

public record SimulacaoQuitacaoResponse(
        int idConta,
        int idFatura,
        BigDecimal valorTotal,
        BigDecimal valorMinimo
) {
}