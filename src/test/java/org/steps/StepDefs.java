package org.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.pages.BasePage;
import org.pages.BooksPage;
import org.pages.LoginPage;
import org.utilities.*;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class StepDefs {

    LoginPage loginPage = new LoginPage();
    BasePage basePage = new BasePage();
    BooksPage booksPage = new BooksPage();

    RequestSpecification givenPart = RestAssured.given();
    ValidatableResponse thenPart;
    Response response;
    JsonPath jp;

    String expectedPathParamValue;
    int ID;
    Map<String, Object> dataMap_API;

    @Given("I logged Library api as a {string}")
    public void i_logged_library_api_as_a(String role) {

        givenPart.header("x-library-token", LibraryUtils.getTokenByRole(role));
    }

    @Given("Accept header is {string}")
    public void accept_header_is(String acceptType) {

        givenPart.accept(acceptType);
    }

    @Given("Path param is {string}")
    public void path_param_is(String pathParam) {

        givenPart.pathParam("id", pathParam);
        expectedPathParamValue = pathParam;
    }

    @Given("Request Content Type header is {string}")
    public void request_content_type_header_is(String contentType) {

        givenPart.contentType(contentType);
    }

    @Given("I create a random {string} as request body")
    public void i_create_a_random_as_request_body(String dataMapType) {

        dataMap_API = LibraryUtils.randomDataMap(dataMapType);

        givenPart.formParams(dataMap_API);
    }

    @Given("I logged in Library UI as {string}")
    public void i_logged_in_library_ui_as(String role) {

        loginPage.login(role);
    }

    @Given("I navigate to {string} page")
    public void i_navigate_to_page(String page) {

        basePage.navigateTo(page);
    }

    @When("I send GET request to {string} endpoint")
    public void i_send_get_request_to_endpoint(String GET_endpoint) {

        response = givenPart.when().get(GET_endpoint);
        thenPart = response.then();
        jp = response.then().extract().jsonPath();

        response.prettyPrint();
    }

    @When("I send POST request to {string} endpoint")
    public void i_send_post_request_to_endpoint(String POST_endpoint) {

        response = givenPart.when().post(POST_endpoint);
        thenPart = response.then();
        jp = response.then().extract().jsonPath();

        response.prettyPrint();

        if (POST_endpoint.contains("book")) {

            ID = jp.getInt("book_id");
            System.out.println("ID = " + ID);
        }
        if (POST_endpoint.contains("user")) {

            ID = jp.getInt("user_id");
            System.out.println("ID = " + ID);
        }
    }

    @Then("status code should be {int}")
    public void status_code_should_be(Integer expectedStatus) {

        thenPart.statusCode(expectedStatus);

//        assertEquals(expectedStatus, response.statusCode());
    }

    @Then("Response Content type is {string}")
    public void response_content_type_is(String expectedContentType) {

        thenPart.contentType(expectedContentType);

        assertEquals(expectedContentType, response.contentType());
    }

    @Then("{string} field should not be null")
    public void field_should_not_be_null(String path) {

        thenPart.body(path, Matchers.notNullValue());

        assertNotNull(jp.getString(path));
    }

    @Then("{string} field should be same with path param")
    public void field_should_be_same_with_path_param(String fieldPath) {

        String actualFieldValue = jp.getString(fieldPath);
        System.out.println("actualFieldValue = " + actualFieldValue);

        assertEquals(expectedPathParamValue, actualFieldValue);
        System.out.println("expectedPathParamValue = " + expectedPathParamValue);
    }

    @Then("following fields should not be null")
    public void following_fields_should_not_be_null(List<String> fieldKeys) {

        for (String each : fieldKeys) {

            thenPart.body(each, notNullValue());
        }
    }

    @Then("the field value for {string} path should be equal to {string}")
    public void the_field_value_for_path_should_be_equal_to(String actualMsgPath, String expectedMsg) {

        assertEquals(expectedMsg, jp.getString(actualMsgPath));
    }

    @Then("UI, Database and API created book information must match")
    public void ui_database_and_api_created_book_information_must_match() {

// DB Check:
        String query = "SELECT * FROM books WHERE id = " + ID;

        DB_Utils.runQuery(query);
        Map<String, String> dataMap_DB = DB_Utils.getRowMap(1);

        assertEquals(String.valueOf(ID), dataMap_DB.get("id"));

    // DB fields:
        String name_DB = dataMap_DB.get("name");
        String isbn_DB = dataMap_DB.get("isbn");
        String year_DB = dataMap_DB.get("year");
        String author_DB = dataMap_DB.get("author");
        String description_DB = dataMap_DB.get("description");

    // API fields:
        String name_API = (String) dataMap_API.get("name");
        String isbn_API = (String) dataMap_API.get("isbn");
        String year_API = (String) dataMap_API.get("year");
        String author_API = (String) dataMap_API.get("author");
        String description_API = (String) dataMap_API.get("description");

    // Assertions:
        assertEquals(name_API, name_DB);
        assertEquals(isbn_API, isbn_DB);
        assertEquals(year_API, year_DB);
        assertEquals(author_API, author_DB);
        assertEquals(description_API, description_DB);

// UI Check:
        BrowserUtils.waitForVisibility(booksPage.searchInput, 3).sendKeys(name_API);
        BrowserUtils.waitForVisibility(booksPage.editBook(name_API), 3).click();

    // UI fields:
        String name_UI = BrowserUtils.waitForVisibility(booksPage.bookName, 3).getAttribute("value");
        String isbn_UI = booksPage.bookIsbn.getAttribute("value");
        String year_UI = booksPage.bookYear.getAttribute("value");
        String author_UI = booksPage.bookAuthor.getAttribute("value");
        String description_UI = booksPage.bookDescription.getAttribute("value");

//        System.out.println("isbn_UI = " + isbn_UI);
//        System.out.println("isbn_API = " + isbn_API);
//        System.out.println("isbn_DB = " + isbn_DB);

    // Assertions:
        assertEquals(name_API, name_UI);
        assertEquals(isbn_API, isbn_UI);
        assertEquals(year_API, year_UI);
        assertEquals(author_API, author_UI);
        assertEquals(description_API, description_UI);
    }

    @Then("created user information should match with Database")
    public void created_user_information_should_match_with_database() {

        String query = "SELECT * FROM users WHERE id = " + ID;

        DB_Utils.runQuery(query);
        Map<String, String> dataMap_DB = DB_Utils.getRowMap(1);

        assertEquals(String.valueOf(ID), dataMap_DB.get("id"));

    // DB fields:
        String full_name_DB = dataMap_DB.get("full_name");
        String email_DB = dataMap_DB.get("email");
//        String user_group_id_DB = dataMap_DB.get("user_group_id");
        String status_DB = dataMap_DB.get("status");
        String address_DB = dataMap_DB.get("address");

    // API fields:
        String full_name_API = (String) dataMap_API.get("full_name");
        String email_API = (String) dataMap_API.get("email");
//        String user_group_id_API = (String) dataMap_API.get("user_group_id");
        String status_API = (String) dataMap_API.get("status");
        String address_API = (String) dataMap_API.get("address");

    // Assertions:
        assertEquals(full_name_API, full_name_DB);
        assertEquals(email_API, email_DB);
//        assertEquals(user_group_id_API, user_group_id_DB);
        assertEquals(status_API, status_DB);
        assertEquals(address_API, address_DB);

    }

    @Then("created user should be able to login Library UI")
    public void created_user_should_be_able_to_login_library_ui() {

        Driver.get().get(ConfigurationReader.getProperty("url"));

        loginPage.login((String) dataMap_API.get("email"), (String) dataMap_API.get("password"));

        assertTrue(basePage.booksPage.isDisplayed());
    }

    @Then("created user name should appear in Dashboard Page")
    public void created_user_name_should_appear_in_dashboard_page() {

        String expectedUser = (String) dataMap_API.get("full_name");
        String actualUser = basePage.loggedInUser.getText();

        BrowserUtils.waitForVisibility(basePage.loggedInUser, 3);
        assertEquals(expectedUser, actualUser);
    }

}
