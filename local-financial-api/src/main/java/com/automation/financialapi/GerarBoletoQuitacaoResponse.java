package com.automation.financialapi;

import java.math.BigDecimal;

public record GerarBoletoQuitacaoResponse(
        int idBoleto,
        int idConta,
        int idFatura,
        BigDecimal valorBoleto,
        int status
) {
}