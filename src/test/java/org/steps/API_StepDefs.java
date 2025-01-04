package org.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.utilities.LibraryUtils;

import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class API_StepDefs {

    RequestSpecification givenPart = RestAssured.given();
    ValidatableResponse thenPart;
    Response response;
    JsonPath jp;

    String expectedPathParamValue;

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

        givenPart.formParams(LibraryUtils.randomDataMap(dataMapType));
    }

    @When("I send GET request to {string} endpoint")
    public void i_send_get_request_to_endpoint(String GET_endpoint) {

        response = givenPart.when().get(GET_endpoint);
        thenPart = response.then();
        jp = response.then().extract().jsonPath();
    }

    @When("I send POST request to {string} endpoint")
    public void i_send_post_request_to_endpoint(String POST_endpoint) {

        response = givenPart.when().post(POST_endpoint);
        thenPart = response.then();
        jp = response.then().extract().jsonPath();
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

}
