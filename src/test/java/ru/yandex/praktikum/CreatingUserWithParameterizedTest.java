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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.praktikum.client.UserClient;
import ru.yandex.praktikum.model.User;
import ru.yandex.praktikum.model.UserCredentials;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;


@RunWith(Parameterized.class)
public class CreatingUserWithParameterizedTest {
    private UserClient userClient;
    private final String email;
    private final String password;
    private final String name;
    private String accessToken;

    public CreatingUserWithParameterizedTest (String email, String password, String name){

        this.email=email;
        this.password=password;
        this.name=name;
    }
    @Parameterized.Parameters
    public static Object[][] dataForTest(){
        return new Object[][]{
                {null, "passwordtest", "nametest"},
                {"emailtest@test.ru", null, "nametest"},
                {"emailtest@test.ru", "passwordtest", null}
        };
    }

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
    @DisplayName("Создание учетной записи с незаполненным обязательным полем")
    @Description("Учетная запись не будет создана без обязательных полей")
    public void creatingUserWithoutObligatoryFields() {

        User user = new User(email, password, name);
        userClient.createUser(user)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .assertThat()
                .body("success", is(false))
                .and()
                .assertThat()
                .body("message", is("Email, password and name are required fields"));

        accessToken = userClient.login(UserCredentials.from(user))
                .assertThat()
                .body("accessToken", notNullValue())
                .extract().path("accessToken");
    }
}

