package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.yandex.praktikum.client.OrderClient;
import ru.yandex.praktikum.client.UserClient;
import ru.yandex.praktikum.model.User;
import ru.yandex.praktikum.model.UserCredentials;
import ru.yandex.praktikum.model.UserGenerator;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class GetOrderWithAuthorizationTest {
    private UserClient userClient;
    private String accessToken;
    private OrderClient orderClient;

    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(
                new RequestLoggingFilter(), new ResponseLoggingFilter(),
                new AllureRestAssured()
        );
    }

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void clearData() {
        userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Получение заказов авторизованным пользователем")
    @Description("Позитивный сценарий получения заказов с авторизацией" )

    public void getOrderWithToken() {
        User user = UserGenerator.getRandom();

        userClient.createUser(user)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .assertThat()
                .body("success", is(true));

        accessToken = userClient.login(UserCredentials.from(user))
                .assertThat()
                .body("accessToken", notNullValue())
                .extract().path("accessToken");

        orderClient.getAllOrders(accessToken)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .assertThat()
                .body("success", is(true))
                .and()
                .assertThat()
                .body("orders", notNullValue());
    }

}
