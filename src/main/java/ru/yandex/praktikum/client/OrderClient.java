package ru.yandex.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.client.base.BurgerRestClient;
import ru.yandex.praktikum.model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends BurgerRestClient {


    private static final String ORDER_URI = BASE_URI + "orders/";

    @Step("Create order")
    public ValidatableResponse createOrderWithoutToken(Order order) {
        return given()
                .spec(getBaseReqSpec())
                .body(order)
                .when()
                .post(ORDER_URI)
                .then();
    }
    @Step("Get order")
    public ValidatableResponse getAllOrders () {
        return given()
                .spec(getBaseReqSpec())
                .when()
                .get(ORDER_URI + "all")
                .then();
    }
}
