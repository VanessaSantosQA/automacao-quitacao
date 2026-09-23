package com.automation.integrations.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

class LocalPaymentApiTest {

    @Test
    @EnabledIfSystemProperty(named = "localApi", matches = "true")
    void deveCriarPagamentoPelaApi() {

        String body = """
                {
                  "idConta": 10001,
                  "idFatura": 1,
                  "valorPagamento": 230.00
                }
                """;

        given()
                .baseUri("http://localhost:8081")
                .contentType("application/json")
                .body(body)

        .when()
                .post("/pagamentos")

        .then()
                .log().all()
                .statusCode(201)
                .body("idPagamento", greaterThan(0))
                .body("idConta", equalTo(10001))
                .body("idFatura", equalTo(1))
                .body("valorPagamento", equalTo(230.0f))
                .body("status", equalTo(0));
    }
}