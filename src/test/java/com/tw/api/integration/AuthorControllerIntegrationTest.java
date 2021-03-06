package com.tw.api.integration;

import com.tw.api.contract.AuthorRequest;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class AuthorControllerIntegrationTest {

    @Value(value = "${local.server.port}")
    private int port;

    @Value(value = "${api.version}")
    private String apiVersion;

    @Test
    public void createAuthor() throws IOException {
        given()
                .port(port)
                .when()
                .contentType(ContentType.JSON)
                .body(AuthorRequest.builder()
                        .name("Yang")
                        .age(30)
                        .build())
                .post(String.format("/api/%s/author", apiVersion))
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .contentType(ContentType.JSON)
                .and().body("id.length()", is(8))
                .and().body("name", is("Yang"))
                .and().body("age", is(30));
    }

    @Test
    public void updateAuthor() throws IOException {
        AuthorRequest authorRequest = AuthorRequest.builder()
                .name("Yang")
                .age(30)
                .build();
        String authorId = given()
                .port(port)
                .when()
                .contentType(ContentType.JSON)
                .body(authorRequest)
                .post(String.format("/api/%s/author", apiVersion))
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .contentType(ContentType.JSON)
                .extract()
                .path("id");

        AuthorRequest updateAuthor = AuthorRequest.builder()
                .name("Yang")
                .age(31)
                .build();
        given()
                .port(port)
                .when()
                .contentType(ContentType.JSON)
                .body(updateAuthor)
                .put(String.format("/api/%s/author/%s", apiVersion, authorId))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON)
                .and().body("id", is(authorId))
                .and().body("name", is("Yang"))
                .and().body("age", is(31));
    }
}
