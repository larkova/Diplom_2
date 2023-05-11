package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.yandex.praktikum.client.OrderClient;
import ru.yandex.praktikum.model.Order;
import java.util.ArrayList;
import java.util.List;
import static org.apache.http.HttpStatus.*;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.is;

public class CreatingOrderWithoutAuthorizationTest {
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
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами без авторизации")
    @Description("Позитивный сценарий создание заказа без авторизации, заказ успешно создается")

    public void creatingOrderWithoutTokenWithIngredients() {

        List<String> ingredients = new ArrayList<>();
        ingredients.add("61c0c5a71d1f82001bdaaa6d");
        ingredients.add("61c0c5a71d1f82001bdaaa6f");
        ingredients.add("61c0c5a71d1f82001bdaaa71");
        ingredients.add("61c0c5a71d1f82001bdaaa72");
        Order order = new Order(ingredients);

        orderClient.createOrderWithoutToken(order)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .assertThat()
                .body("success", is(true));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов без авторизации")
    @Description("Негативный сценарий создание заказа без ингредиентов без авторизации, вернётся код ответа 400" +
            "Internal Server Error")

    public void creatingOrderWithoutTokenWithoutIngredients() {
        List <String> ingredients = new ArrayList<>();
        Order order = new Order(ingredients);

        orderClient.createOrderWithoutToken(order)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .assertThat()
                .body("success", is(false))
                .and()
                .assertThat()
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа c неправильным хешом ингредиентов без авторизации пользователя")
    @Description("Негативный сценарий создание заказа с неправильным хешом ингредиентов без авторизации, вернётся код ответа 500" +
            "Internal Server Error")

    public void creatingOrderWithoutTokenWitWrongHashOfIngredients() {
        List <String> ingredients = new ArrayList<>();
        ingredients.add("61c0c5a71d1f82001bdaaa6d_Test");
        Order order = new Order(ingredients);

        orderClient.createOrderWithoutToken(order)
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);

    }

}
