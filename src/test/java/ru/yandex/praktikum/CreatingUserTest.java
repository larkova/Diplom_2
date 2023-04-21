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
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreatingUserTest {
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
    @DisplayName("Создание учетной записи пользователя со всеми корректно заполненными полями")
    @Description("Учетнаязапись пользователя успешно создается со всеми заполненными полями: email, пароль, имя" )

    public void userCanBeCreatedWithValidData() {
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

    }

    @Test
    @DisplayName("Создание двух учетных записей пользователей с одинаковым логином")
    @Description("Нельзя создать двух пользователей с одинаковым логином")
    public void twoSameLoginCanNotBeCreated() {
        User user = new User ("Testlogin@mail.ru", "Testpassword", "Elena");
        User secondUser = new User ("Testlogin@mail.ru", "Testpassword1234", "Alex");

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

        userClient.createUser(secondUser)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .assertThat()
                .body("success", is(false))
                .and()
                .assertThat()
                .body("message", is( "User already exists"));

    }

}
