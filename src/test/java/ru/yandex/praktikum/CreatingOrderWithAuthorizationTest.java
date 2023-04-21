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
import ru.yandex.praktikum.model.Order;
import ru.yandex.praktikum.model.User;
import ru.yandex.praktikum.model.UserCredentials;
import ru.yandex.praktikum.model.UserGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreatingOrderWithAuthorizationTest {

        private UserClient userClient;
        private OrderClient orderClient;
        private String accessToken;

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
    @DisplayName("Создание заказа с ингредиентами авторизованным пользователем")
    @Description("Позитивный сценарий создание заказа с ингредиентами авторизирванным пользователем, заказ успешно создается")

    public void creatingOrderWithTokenWithIngredients() {

        User user = UserGenerator.getRandom();
        List<String> ingredients = new ArrayList<>();
        ingredients.add("61c0c5a71d1f82001bdaaa6d");
        ingredients.add("61c0c5a71d1f82001bdaaa6f");

        Order order = new Order(ingredients);

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

        userClient.createOrderWithToken(order, accessToken)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .assertThat()
                .body("success", is(true));
    }
    @Test
    @DisplayName("Создание заказа без ингредиентов авторизовааным пользователем")
    @Description("Негативный сценарий создание заказа без ингредиентов авторизованным пользователем, вернётся код ответа 400")

    public void creatingOrderWithTokenWithoutIngredients() {

        User user = UserGenerator.getRandom();
        List<String> ingredients = new ArrayList<>();
        Order order = new Order(ingredients);

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

        userClient.createOrderWithToken(order, accessToken)
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
    @DisplayName("Создание заказа с неверным хешом ингредиентов авторизованным пользователем")
    @Description("Негативный сценарий создание заказа с неправильным хешом ингредиентов авторизованным пользователем, вернётся код ответа 500")

    public void creatingOrderWithTokenWithWrongHashOfIngredients() {

        User user = UserGenerator.getRandom();
        List<String> ingredients = new ArrayList<>();
        ingredients.add("61c0c5a71d1f82001bdaaa6d_Test");

        Order order = new Order(ingredients);

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

        userClient.createOrderWithToken(order, accessToken)
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

}
