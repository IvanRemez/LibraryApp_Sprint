package org.utilities;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LibraryUtils {

    public static Map<String, String> returnCredentials(String role) {

        String email = "";
        String password = "";

        switch (role) {

            case "librarian":
                email = ConfigurationReader.getProperty("librarian_username");
                password = System.getenv("librarian_password");
                break;
            case "student":
                email = ConfigurationReader.getProperty("student_username");
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

    public static String getToken(String email, String password) {

        Map<String, String> credentials = new LinkedHashMap<>();
        credentials.put("email", email);
        credentials.put("password", password);

        JsonPath jp = RestAssured.given()
//                .log().uri()
                .contentType(ContentType.URLENC)
                .accept(ContentType.JSON)
                .formParams(credentials)
                .when()
                .post("/login")
                .prettyPeek()
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().jsonPath();

        return jp.getString("token");
    }

    public static String getTokenByRole(String role) {

        Map<String, String> roleCredentials = returnCredentials(role);
        String email = roleCredentials.get("email");
        String password = roleCredentials.get("password");

        return getToken(email, password);
    }

    public static Map<String, Object> randomDataMap(String mapType) {

        Faker faker = new Faker();
        Map<String, Object> dataMap = new HashMap<>();

        switch (mapType) {

            case "book":

                dataMap.put("name", "IR-" + faker.book().title());
                dataMap.put("isbn", faker.code().isbn10());
                dataMap.put("year", String.valueOf(faker.number().numberBetween(1850, 2010)));
                dataMap.put("author", faker.book().author());
                dataMap.put("book_category_id", String.valueOf(faker.number().numberBetween(1, 20)));
                dataMap.put("description", faker.chuckNorris().fact());
                break;

            case "user":
                dataMap.put("full_name", faker.name().fullName());
                dataMap.put("email", faker.name().lastName() + faker.number().numberBetween(1, 99) + "@library");
                dataMap.put("password", "NorrisChuck");
                dataMap.put("user_group_id", 3);
                dataMap.put("status", "ACTIVE");
                dataMap.put("start_date", "2020-01-01");
                dataMap.put("end_date", "2023-01-01");
                dataMap.put("address", "123 TheWay");

//                switch (userType){
//                    case "admin":
//                        user.put("user_group_id",1);
//                        break;
//                    case "librarian":
//                        user.put("user_group_id",2);
//                        break;
//                    case "student":
//                        user.put("user_group_id",3);
//                        break;
//                    default:
//                        throw new RuntimeException("Invalid User Type Entry :\n>> " + userType + " <<");
//
//                }
                break;

            default:
                throw new RuntimeException("INVALID Map Type");
        }
        System.out.println("dataMap = " + dataMap);

        return dataMap;
    }
}
