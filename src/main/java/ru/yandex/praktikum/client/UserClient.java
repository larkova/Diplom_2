package ru.yandex.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.client.base.BurgerRestClient;
import ru.yandex.praktikum.model.Order;
import ru.yandex.praktikum.model.User;
import ru.yandex.praktikum.model.UserCredentials;

import java.util.Random;

import static io.restassured.RestAssured.given;

public class UserClient extends BurgerRestClient {


    private static final String USER_URI = BASE_URI + "auth/";

    @Step("Create user {user}")
    public ValidatableResponse createUser (User user) {
        return given()
                .spec(getBaseReqSpec())
                .body(user)
                .when()
                .post(USER_URI + "register")
                .then();
    }

    @Step("Login as {courierCredentials}")
    public ValidatableResponse login(UserCredentials userCredentials) {
        return given()
                .spec(getBaseReqSpec())
                .body(userCredentials)
                .when()
                .post(USER_URI+ "login")
                .then();
    }

    @Step("Delete user {token}")
    public ValidatableResponse delete (String accessToken) {
        return given()
                .spec(getBaseReqSpec())
                .header("Authorization", accessToken)
                .when()
                .delete(USER_URI + "user")
                .then();
    }

    @Step("Changing {userCredentials} without authorization")
    public ValidatableResponse changingUserWithoutToken(User user) {
        return given()
                .spec(getBaseReqSpec())
                .body(user)
                .when()
                .patch(USER_URI+ "user")
                .then();
    }

    @Step("Changing {userCredentials} with authorization")
    public ValidatableResponse changingUserWithToken(User user, String accessToken) {
        return given()
                .spec(getBaseReqSpec())
                .header("Authorization", accessToken)
                .body(user)
                .when()
                .patch(USER_URI+ "user")
                .then();
    }

    @Step("Create order with token")
    public ValidatableResponse createOrderWithToken(Order order, String accessToken) {
        return given()
                .spec(getBaseReqSpec())
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(BASE_URI+ "orders")
                .then();
    }
    @Step("Get with token")
    public ValidatableResponse getAllOrders (String accessToken) {
        return given()
                .spec(getBaseReqSpec())
                .header("Authorization", accessToken)
                .when()
                .get(BASE_URI+ "orders/all")
                .then();
    }

}
