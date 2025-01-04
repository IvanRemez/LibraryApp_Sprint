package org.utilities;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

import java.util.HashMap;
import java.util.Map;

public class LibraryUtils {

    public static Map<String, String> returnCredentials(String role) {

        String email = "";
        String password = "";

        switch (role) {

            case "librarian":
                email = System.getenv("librarian_username");
                password = System.getenv("librarian_password");
                break;
            case "student":
                email = System.getenv("student_username");
                password = System.getenv("student_password");
                break;
            default:
                throw new RuntimeException("INVALID User Role");
        }

        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", email);
        credentials.put("password", password);

        return credentials;
    }

    public static String getTokenByRole(String role) {

        JsonPath jp = RestAssured.given()
                .log().all()
                .contentType(ContentType.URLENC)
                .accept(ContentType.JSON)
                .formParams(returnCredentials(role))
                .when()
                .post("/login")
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().jsonPath();

        return jp.getString("token");
    }

    public static Map<String, Object> randomDataMap(String mapType) {

        Faker faker = new Faker();
        Map<String, Object> dataMap = new HashMap<>();

        switch (mapType) {

            case "book":

                dataMap.put("name", faker.book().title());
                dataMap.put("isbn", faker.code().isbn10());
                dataMap.put("year", String.valueOf(faker.number().numberBetween(1850, 2010)));
                dataMap.put("author", faker.book().author());
                dataMap.put("book_category_id", String.valueOf(faker.number().numberBetween(1, 20)));
                dataMap.put("description", faker.chuckNorris().fact());
                break;

            case "user":

                break;

            default:
                throw new RuntimeException("INVALID Map Type");
        }

        System.out.println("dataMap = " + dataMap);
        return dataMap;
    }
}
