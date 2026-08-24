package com.automation.financialapi.api;

import com.automation.financialapi.client.PagamentoApiClient;
import com.automation.financialapi.dto.PagamentoRequest;
import com.automation.financialapi.validator.PagamentoApiValidator;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PagamentoApiTest {

    private PagamentoApiClient client;
    private PagamentoApiValidator validator;

    @BeforeEach
    void prepararTeste() {
        client = new PagamentoApiClient();
        validator = new PagamentoApiValidator();
    }

    @Test
    void deveConsultarPagamentoComSucesso() {

        Response response =
                client.buscarPorId(123);

        validator.validarConsultaComSucesso(
                response,
                123,
                "PROCESSADO"
        );
    }

    @Test
    void deveRetornar404QuandoPagamentoNaoExistir() {

        Response response =
                client.buscarPorId(999);

        validator.validarPagamentoNaoEncontrado(response);
    }

    @Test
    void deveRetornar400QuandoIdForInvalido() {

        Response response =
                client.buscarPorId(0);

        validator.validarRequisicaoInvalida(response);
    }

    @Test
    void deveCriarPagamentoComSucesso() {

        PagamentoRequest request =
                new PagamentoRequest(
                        456,
                        "PENDENTE"
                );

        Response response =
                client.criar(request);

        validator.validarCriacaoComSucesso(
                response,
                456,
                "PENDENTE"
        );
    }

    @Test
    void deveRetornar400AoCriarPagamentoComIdInvalido() {

        PagamentoRequest request =
                new PagamentoRequest(
                        0,
                        "PENDENTE"
                );

        Response response =
                client.criar(request);

        validator.validarRequisicaoInvalida(response);
    }

    @Test
    void deveRetornar400AoCriarPagamentoComStatusVazio() {

        PagamentoRequest request =
                new PagamentoRequest(
                        456,
                        ""
                );

        Response response =
                client.criar(request);

        validator.validarRequisicaoInvalida(response);
    }
}