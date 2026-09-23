package com.automation.integrations.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class LocalQuitacaoSimulationApiTest {

    @Test
    @EnabledIfSystemProperty(named = "localApi", matches = "true")
    void deveSimularQuitacaoComDadosDaFatura() {

        String body = """
                {
                  "idConta": 10001
                }
                """;

        given()
                .baseUri("http://localhost:8081")
                .contentType("application/json")
                .body(body)

        .when()
                .post("/quitacao/simular")

        .then()
                .log().all()
                .statusCode(200)
                .body("idConta", equalTo(10001))
                .body("idFatura", equalTo(1))
                .body("valorTotal", equalTo(250.0f))
                .body("valorMinimo", equalTo(200.0f));
    }
}