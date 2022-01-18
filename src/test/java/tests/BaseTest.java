package tests;

import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import utils.LoadProperties;

import static io.restassured.RestAssured.given;

public class BaseTest {

    @BeforeTest
    public void setUp() {
        String key = LoadProperties.getProperty("Bearer");
        RestAssured.baseURI = "https://gorest.co.in/public/v1";
        RestAssured.requestSpecification = given().header("Accept", "application/json").header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + key)
                .log().all();
        RestAssured.responseSpecification = new ResponseSpecBuilder().log(LogDetail.ALL).build();
    }

    @DataProvider(name = "createUserFailCases")
    public Object[][] userData() {
        return new Object[][]{
                {"", "a@example.com", "male", "active"},
                {"Jack Jones", "", "female", "active"},
                {"Jack Jones", "a@example.com", "", "active"},
                {"Jack Jones", "a@example.com", "male", ""},
                {"Jack Jones", "axamls", "male", "active"},
                {"Jack Jones", "a@example.com", "m", "active"},
                {"Jack Jones", "a@example.com", "male", "jsijeoksjs"},
                {"Jack Jones", "a@example.com", "male", "jsk"}
        };
    }

    @DataProvider(name = "createPostFailCases")
    public Object[][] postData() {
        return new Object[][]{
                {"", "Cordae Album is fire"},
                {"Top 10 Albums", ""}
        };
    }

    @DataProvider(name = "createCommentFailCases")
    public Object[][] commentData() {
        return new Object[][] {
                {"", "Henry@example.com", "Hello"},
                {"Henry Jones", "", "Henry was here"},
                {"Henry Jones", "Henry@example.com", ""},
                {"Jack Daniels", "JackDan.com", "Will email work?"}
        };
    }

    @DataProvider(name = "createTodoFailCases")
    public Object[][] todoData() {
        return new Object[][] {
                {"", "2022-01-02", "completed"},
                {"Take out trash", "2022-01-02", ""},
                {"Take out trash", "2022-01-02", "word"}
        };
    }
}
