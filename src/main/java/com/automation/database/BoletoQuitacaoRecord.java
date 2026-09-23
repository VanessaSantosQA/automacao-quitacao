package com.automation.database;

import java.math.BigDecimal;

public record BoletoQuitacaoRecord(
        int idBoleto,
        int idConta,
        int idFatura,
        BigDecimal valorBoleto,
        int status
) {
}