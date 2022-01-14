package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.module.jsv.JsonSchemaValidator;
import models.CreatePostData;
import models.Data;
import models.PostResponse;
import models.UserResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.ValueMatcher;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import utils.LoadProperties;


import java.util.Properties;

import static io.restassured.RestAssured.*;

public class GoRestTest {

    @BeforeTest
    public void setUp() {
        String key = LoadProperties.getProperty("Bearer");
        RestAssured.baseURI = "https://gorest.co.in/public/v1/";
        RestAssured.requestSpecification = given().header("Accept", "application/json").header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + key)
                .log().all();
        RestAssured.responseSpecification = new ResponseSpecBuilder().log(LogDetail.ALL).build();
    }

    // When there is nobody you can start it with get because of restAssured
    @Test
    public void testListUsers() {
        String responseBody = get("users")
                .then().spec(responseSpecification).assertThat().statusCode(200).extract().response().asString();
    }

    @Test
    public void testCreateUser() throws JsonProcessingException {
        String time = "" + System.currentTimeMillis();
//        String requestBody = "{\n" +
//        "    \"name\": \"George Lincoln\",\n" +
//                "    \"email\": \"George"+ time +"@example.com\",\n" +
//                "    \"gender\": \"male\",\n" +
//                "    \"status\": \"active\"\n" +
//                "}";

        // You can use a hashmap to import data to an api. Predominately used
//        HashMap<String, Object> reqMap = new HashMap<>();
//        reqMap.put("name", "George Lincoln");
//        reqMap.put("email", "George" + time + "@example.com");
//        reqMap.put("gender", "male");
//        reqMap.put("status", "active");
//
        // You can use this to import a json file with the data for api
//        File file = new File("src/test/resources/payload.json");

        ObjectMapper mapper = new ObjectMapper();

        // Setting data for new user
        Data data = new Data();
        data.setName("George Lincoln");
        data.setEmail("George" + time + "@example.com");
        data.setGender("male");
        data.setStatus("active");


       UserResponse userResponse =  requestSpecification.body(data)
                .when().post("users")
                .then().spec(responseSpecification).assertThat().statusCode(201)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("createUserSchema.json")).extract()
                .response().as(UserResponse.class);

        // This gets the data from the Data file
        String requestJson = mapper.writeValueAsString(data);

        // This gets the response data from the api call
        String responseJson = mapper.writeValueAsString(userResponse.getData());

        // The CustomComparotor part is for ignoring the id, when asserting the values.
        JSONAssert.assertEquals(requestJson, responseJson,
                new CustomComparator( JSONCompareMode.STRICT_ORDER,
                        new Customization("id", new ValueMatcher<Object>() {
                            @Override
                            public boolean equal(Object o, Object t1) {
                                return true;
                            }
                        })
                ));
        // This compares all of the values assigned with the response from the api call
//       JSONAssert.assertEquals(requestJson, responseJson, JSONCompareMode.LENIENT);
//       Assert.assertTrue(userResponse.getData().getId() > 0);
    }

    @Test
    public void testGetUser() throws JsonProcessingException {
        // Create User
        String time = "" + System.currentTimeMillis();

        ObjectMapper mapper = new ObjectMapper();
        Data data = new Data();
        data.setName("Jorge Linares");
        data.setEmail("Jorge" + time + "@example.com");
        data.setGender("male");
        data.setStatus("active");

        String responseBody = requestSpecification.body(data)
                .when().post("users")
                .then().spec(responseSpecification).assertThat().statusCode(201).extract().response().asString();
        // Get user id
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONObject data1 = jsonObject.getJSONObject("data");
        int userID = data1.getInt("id");

        UserResponse getUser = get("users/" + userID)
                .then().spec(responseSpecification).assertThat().statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("getUserSchema.json"))
                .extract().response().as(UserResponse.class);

        String requestJson = mapper.writeValueAsString(data);
        String responseJson = mapper.writeValueAsString(getUser.getData());
        JSONAssert.assertEquals(requestJson, responseJson,
                new CustomComparator(JSONCompareMode.STRICT_ORDER,
                        new Customization("id", new ValueMatcher<Object>() {
                            @Override
                            public boolean equal(Object o, Object t1) {
                                return true;
                            }
                        })
                )
        );

    }

    @Test
    public void testUpdateUser() throws JsonProcessingException {
        // Create User
        String timeStamp = "" + System.currentTimeMillis();

        ObjectMapper mapper = new ObjectMapper();
        Data data = new Data();
        data.setName("George Lincoln");
        data.setEmail("George" + timeStamp + "@example");
        data.setGender("male");
        data.setStatus("active");

        String responseBody = requestSpecification.body(data)
                .when().post("users")
                .then().spec(responseSpecification).assertThat().statusCode(201)
                .extract().response().asString();

        // Get User id
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONObject data1 = jsonObject.getJSONObject("data");
        int userID = data1.getInt("id");


        String time = "" + System.currentTimeMillis();

        data.setName("Patrick Lincoln");
        data.setEmail("Patrick" + time + "@example.com");
        data.setGender("male");
        data.setStatus("active");
        UserResponse update = requestSpecification.body(data)
                .when().patch("users/" + userID)
                .then().spec(responseSpecification).assertThat().statusCode(200)
                .extract().response().as(UserResponse.class);

        String requestJson = mapper.writeValueAsString(data);
        String responseJson = mapper.writeValueAsString(update.getData());
        JSONAssert.assertEquals(requestJson, responseJson,
                new CustomComparator( JSONCompareMode.STRICT_ORDER,
                        new Customization("id", new ValueMatcher<Object>() {
                            @Override
                            public boolean equal(Object o, Object t1) {
                                return true;
                            }
                        })
                ));

    }

    @Test
    public void testDeleteUser() {
        String time = "" + System.currentTimeMillis();

        Data data = new Data();
        data.setName("Pat Bev");
        data.setEmail("Pat" + time + "@example.com");
        data.setGender("male");
        data.setStatus("active");

        String responseBody = requestSpecification.body(data)
                .when().post("users")
                .then().spec(responseSpecification).assertThat().statusCode(201).extract().response().asString();

        JSONObject jsonObject = new JSONObject(responseBody);
        JSONObject data1 = jsonObject.getJSONObject("data");
        int userID = data1.getInt("id");

        String responseBody2 = delete("users/" + userID)
                .then().spec(responseSpecification).assertThat().statusCode(204).extract().response().asString();
    }

    @Test
    public void testCreatePost() throws JsonProcessingException {
        String time = "" + System.currentTimeMillis();

        ObjectMapper mapper = new ObjectMapper();
        Data data = new Data();
        data.setName("Jordan Michael");
        data.setEmail("JM" + time + "@example.com");
        data.setGender("male");
        data.setStatus("active");


        UserResponse responseBody = requestSpecification.body(data)
                .when().post("users")
                .then().spec(responseSpecification).assertThat()
                .statusCode(201).extract().response().as(UserResponse.class);


        int userID = responseBody.getData().getId();




        CreatePostData postData = new CreatePostData();
        postData.setTitle("SuperBowl");
        postData.setBody("Packers beat the Chiefs 35 - 31");

        PostResponse createPost = requestSpecification.body(postData)
                .when().post("users/" + userID + "/posts")
                .then().spec(responseSpecification).assertThat().statusCode(201)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("createPostSchema.json")).
                extract().response().as(PostResponse.class);

        String requestJson = mapper.writeValueAsString(postData);
        String responseJson = mapper.writeValueAsString(createPost.getData());

        JSONAssert.assertEquals(requestJson, responseJson,
                new CustomComparator(JSONCompareMode.STRICT_ORDER,
                        new Customization("user_id", new ValueMatcher<Object>() {
                            @Override
                            public boolean equal(Object o, Object t1) {
                                return true;
                            }
                        })));

    }

    @Test
    public void testListPost() throws JsonProcessingException {
        // Create a user
        String time = "" + System.currentTimeMillis();

        ObjectMapper mapper = new ObjectMapper();

        Data data = new Data();
        data.setName("Jordan Michael");
        data.setEmail("JM" + time + "@example.com");
        data.setGender("male");
        data.setStatus("active");

        UserResponse responseBody = requestSpecification.body(data)
                .when().post("users")
                .then().spec(responseSpecification).assertThat().statusCode(201)
                .extract().response().as(UserResponse.class);

        // Get user id

        int userID = responseBody.getData().getId();

        // Create post
        CreatePostData createPostData = new CreatePostData();
        createPostData.setTitle("Superbowl");
        createPostData.setBody("Packers beat the Chiefs 35 - 31");

        String postResponseBody = requestSpecification.body(createPostData)
                .when().post("users/" + userID + "/posts")
                .then().spec(responseSpecification).assertThat().statusCode(201).extract()
                .response().asString();

        // GET POST
        CreatePostData getPost = get("users/" + userID + "/posts")
                .then().spec(responseSpecification).assertThat()
                .statusCode(200).body(JsonSchemaValidator.matchesJsonSchemaInClasspath("listPostSchema.json"))
                .extract().response().as(CreatePostData.class);

        String requestJson = mapper.writeValueAsString(createPostData);
        String responseJson = mapper.writeValueAsString(getPost.getBody());
        JSONAssert.assertEquals(requestJson, responseJson,
                new CustomComparator(JSONCompareMode.STRICT_ORDER,
                        new Customization("id", new ValueMatcher<Object>() {
                            @Override
                            public boolean equal(Object o, Object t1) {
                                return true;
                            }
                        })
                )
        );
    }

    @Test
    public void testCreateComment() {
        // Create User
        String time = "" + System.currentTimeMillis();
        String requestBody = "{\n" +
                "    \"name\": \"Jordan Michael\",\n" +
                "    \"email\": \"JM"+ time +"@example.com\",\n" +
                "    \"gender\": \"male\",\n" +
                "    \"status\": \"active\"\n" +
                "}";
        String responseBody = requestSpecification.body(requestBody)
                .when().post("users")
                .then().spec(responseSpecification).assertThat().statusCode(201).extract().response().asString();

        // Get the User ID
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONObject data = jsonObject.getJSONObject("data");
        int userID = data.getInt("id");

        // Creating post
        String createPost = "{\n" +
                "    \"title\": \"Aaron Rodgers is the GOAT\",\n" +
                "    \"body\": \"Packers beat the Chiefs 35 - 31\"\n" +
                "}";
        String postResponseBody = requestSpecification.body(createPost)
                .when().post("users/" + userID + "/posts")
                .then().spec(responseSpecification).assertThat().statusCode(201).extract().response().asString();

        //get post id
        JSONObject jsonObject1 = new JSONObject(postResponseBody);
        JSONObject postData = jsonObject1.getJSONObject("data");
        int postID = postData.getInt("id");

        // Create comment
        String createComment = "{\n" +
                "    \"name\": \"Joke Looper\",\n" +
                "    \"email\": \"Joke@example.com\",\n" +
                "    \"body\": \"Yea he's better than Brady\"\n" +
                "}";
        String commentResponseBody = requestSpecification.body(createComment)
                .when().post("posts/" + postID + "/comments")
                .then().spec(responseSpecification).assertThat().statusCode(201)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("createCommentSchema.json")).extract().response().asString();
    }


    @Test
    public void testGetComment() {
        // Create User
        String time = "" + System.currentTimeMillis();
        String requestBody = "{\n" +
                "    \"name\": \"Mike Jack\",\n" +
                "    \"email\": \"MJ"+ time +"@example.com\",\n" +
                "    \"gender\": \"male\",\n" +
                "    \"status\": \"active\"\n" +
                "}";
        String responseBody = requestSpecification.body(requestBody)
                .when().post("users")
                .then().spec(responseSpecification).assertThat().statusCode(201).extract().response().asString();

        // Get the User ID
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONObject data = jsonObject.getJSONObject("data");
        int userID = data.getInt("id");

        // Creating post
        String createPost = "{\n" +
                "    \"title\": \"Aaron Rodgers is the MVP\",\n" +
                "    \"body\": \"Packers beat the Chiefs 35 - 31\"\n" +
                "}";
        String postResponseBody = requestSpecification.body(createPost)
                .when().post("users/" + userID + "/posts")
                .then().spec(responseSpecification).assertThat().statusCode(201).extract().response().asString();

        //get post id
        JSONObject jsonObject1 = new JSONObject(postResponseBody);
        JSONObject postData = jsonObject1.getJSONObject("data");
        int postID = postData.getInt("id");

        // Create comment
        String createComment = "{\n" +
                "    \"name\": \"Josh Looper\",\n" +
                "    \"email\": \"Josh@example.com\",\n" +
                "    \"body\": \"Best Quarterback to ever play the game\"\n" +
                "}";
        String commentResponseBody = requestSpecification.body(createComment)
                .when().post("posts/" + postID + "/comments")
                .then().spec(responseSpecification).assertThat().statusCode(201).extract().response().asString();

        // Get Comment id
        JSONObject jsonObject2 = new JSONObject(commentResponseBody);
        JSONObject commentData = jsonObject2.getJSONObject("data");
        int commentID = commentData.getInt("post_id");

        // Get Comment
        String getComments = get("posts/" + commentID + "/comments")
                .then().assertThat().statusCode(200).body(JsonSchemaValidator.matchesJsonSchemaInClasspath("getCommentSchema.json"))
                .extract().response().asString();

    }

    @Test
    public void testCreateTodo() {
        // Create User
        String time = "" + System.currentTimeMillis();
        String requestBody = "{\n" +
                "    \"name\": \"Jordan Michael\",\n" +
                "    \"email\": \"JM"+ time +"@example.com\",\n" +
                "    \"gender\": \"male\",\n" +
                "    \"status\": \"active\"\n" +
                "}";

        String responseBody = requestSpecification.body(requestBody)
                .when().post("users")
                .then().spec(responseSpecification).assertThat().statusCode(201).extract().response().asString();

        // Get user id
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONObject data = jsonObject.getJSONObject("data");
        int userID = data.getInt("id");

        // Create Todo
        String todoBody = "{\n" +
                "    \"title\": \"Take out trash\",\n" +
                "    \"due_on\": \"2022-01-02\",\n" +
                "    \"status\": \"completed\"\n" +
                "}";
        String response = requestSpecification.body(todoBody)
                .when().post("users/" + userID + "/todos")
                .then().spec(responseSpecification).assertThat().statusCode(201)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("createTodoSchema.json")).extract().response().asString();
    }


    @Test
    public void testGetTodo() {
        // Create User
        String time = "" + System.currentTimeMillis();
        String requestBody = "{\n" +
                "    \"name\": \"Jordy Nelson\",\n" +
                "    \"email\": \"Jordy"+ time +"@example.com\",\n" +
                "    \"gender\": \"male\",\n" +
                "    \"status\": \"active\"\n" +
                "}";
        String responseBody = requestSpecification.body(requestBody)
                .when().post("users")
                .then().spec(responseSpecification).assertThat().statusCode(201).extract().response().asString();

        // Get the User ID
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONObject data = jsonObject.getJSONObject("data");
        int userID = data.getInt("id");

        // Create todo
        String todoBody = "{\n" +
                "    \"title\": \"Take out trash\",\n" +
                "    \"due_on\": \"2022-01-02\",\n" +
                "    \"status\": \"completed\"\n" +
                "}";
        String response2 = requestSpecification.body(todoBody)
                .when().post("users/" + userID + "/todos")
                .then().spec(responseSpecification).assertThat().statusCode(201).extract().response().asString();

        JSONObject jsonObject2 = new JSONObject(response2);
        JSONObject data2 = jsonObject2.getJSONObject("data");
        int ToDo_ID = data2.getInt("id");

        // Get todo
        String getTodo = requestSpecification.when().get("todos/" + ToDo_ID)
                .then().spec(responseSpecification).assertThat().statusCode(200).body(JsonSchemaValidator.matchesJsonSchemaInClasspath("getTodoSchema.json"))
                .extract().response().asString();
    }

// Next assignment
    // Create models for each test. Put them in folders to organize them
    // Make assertions using the data you have and responding data
}