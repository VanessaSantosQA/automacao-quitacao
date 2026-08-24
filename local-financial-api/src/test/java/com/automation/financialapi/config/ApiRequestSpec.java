package com.automation.financialapi.config;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public final class ApiRequestSpec {

    private ApiRequestSpec() {
    }

    public static RequestSpecification padrao() {

        return new RequestSpecBuilder()
                .setBaseUri(ApiConfig.baseUri())
                .setContentType("application/json")
                .addFilter(new AllureRestAssured())
                .build();
    }
}