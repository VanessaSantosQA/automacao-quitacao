package com.automation.financialapi.client;

import com.automation.financialapi.config.ApiRequestSpec;
import com.automation.financialapi.dto.PagamentoRequest;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class PagamentoApiClient {

    public Response buscarPorId(long id) {

        return given()
                .spec(ApiRequestSpec.padrao())
        .when()
                .get("/api/pagamentos/" + id);
    }

    public Response criar(PagamentoRequest request) {

        return given()
                .spec(ApiRequestSpec.padrao())
                .body(request)
        .when()
                .post("/api/pagamentos");
    }
}