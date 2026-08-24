package com.automation.financialapi.validator;

import com.automation.financialapi.dto.PagamentoResponse;
import io.restassured.response.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PagamentoApiValidator {

    public void validarConsultaComSucesso(
            Response response,
            long idEsperado,
            String statusEsperado
    ) {
        assertEquals(200, response.statusCode());

        PagamentoResponse pagamento =
                response.as(PagamentoResponse.class);

        assertEquals(idEsperado, pagamento.id());
        assertEquals(statusEsperado, pagamento.status());
    }

    public void validarCriacaoComSucesso(
            Response response,
            long idEsperado,
            String statusEsperado
    ) {
        assertEquals(201, response.statusCode());

        PagamentoResponse pagamento =
                response.as(PagamentoResponse.class);

        assertEquals(idEsperado, pagamento.id());
        assertEquals(statusEsperado, pagamento.status());
    }

    public void validarPagamentoNaoEncontrado(Response response) {
        assertEquals(404, response.statusCode());
    }

    public void validarRequisicaoInvalida(Response response) {
        assertEquals(400, response.statusCode());
    }
}