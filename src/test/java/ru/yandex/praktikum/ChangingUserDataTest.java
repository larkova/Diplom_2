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
import ru.yandex.praktikum.client.UserClient;
import ru.yandex.praktikum.model.User;
import ru.yandex.praktikum.model.UserCredentials;
import ru.yandex.praktikum.model.UserGenerator;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class ChangingUserDataTest {
    private UserClient userClient;
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
    @DisplayName("Изменение данных пользователя без авторизации")
    @Description("Пользователь не может изменить данные без авторизации, при такой попытке появляется ошибка")

    public void userCanNotChangeDataWithoutAuthorization() {
        User user = UserGenerator.getRandom();
        User userNew = new User ("Testlogin@mail.ru", "Testpassword", "Elena");

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

        userClient.changingUserWithoutToken(userNew)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .assertThat()
                .body("success", is(false))
                .and()
                .assertThat()
                .body("message", is("You should be authorised"));

    }
    @Test
    @DisplayName("Изменение данных пользователя c авторизацией")
    @Description("Позитивный сценарий изменения данных авторизированным пользователем")

    public void userCanChangeDataWithAuthorization() {
        User user = UserGenerator.getRandom();
        User userNew = new User ("Testlogin@mail.ru", "Testpassword", "Elena");

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

        userClient.changingUserWithToken(userNew, accessToken)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .assertThat()
                .body("success", is(true));

        accessToken = userClient.login(UserCredentials.from(userNew))
                .assertThat()
                .body("accessToken", notNullValue())
                .extract().path("accessToken");

    }

}
