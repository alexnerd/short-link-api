/*
 * Copyright 2023 Aleksey Popov <alexnerd.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.alexnerd.getting.shortlink.boundary;

import com.alexnerd.shortlink.buisness.boundary.ShortLinksResource;
import com.alexnerd.shortlink.buisness.control.ShortLinkStore;
import com.alexnerd.shortlink.buisness.entity.ShortLink;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;


@QuarkusTest
@TestHTTPEndpoint(ShortLinksResource.class)
public class ShortLinksResourceTest {

    @Inject
    ShortLinkStore store;

    @ConfigProperty(name = "application.host")
    String defaultHost;

    private final String URL = "/short-link";

    @Test
    @DisplayName("Should accept and return created link")
    public void shouldAcceptCreateShortLinkTest() {
        ShortLink shortLink = new ShortLink();
        shortLink.redirectLink = "https://redirect-link.ru";
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(shortLink)
                .expect()
                .statusCode(201)
                .when()
                .post(URL);

        assertNotNull(response.getHeader("Location"));
    }

    @Test
    @DisplayName("Should not accept short link with short link")
    public void shouldNotAcceptCreateShortLinkWithShortLinkTest() {
        ShortLink shortLink = new ShortLink();
        shortLink.shortLink = "df_er5TY";
        shortLink.redirectLink = "https://redirect-link.ru";
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(shortLink)
                .expect()
                .statusCode(422)
                .when()
                .post(URL);
    }

    @Test
    @DisplayName("Should not accept short link with short link")
    public void shouldNotAcceptCreateShortLinkWithoutFullLinkTest() {
        ShortLink shortLink = new ShortLink();
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{}")
                .expect()
                .statusCode(400)
                .when()
                .post(URL);
    }

    @Test
    @DisplayName("Should return 404 for empty body for create short link")
    public void shouldBadRequestForEmptyBodyCreateShortLinkTest() {
        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON)
                .post(URL)
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should redirect from short link")
    public void shouldRedirectFromShortLinkTest() {
        ShortLink shortLink = store.getShortLink("redirect_link");

        Response response = given()
                .redirects()
                .follow(false)
                .expect()
                .statusCode(303)
                .when()
                .get("/" + shortLink.shortLink);

        assertTrue(response.getHeader("Location").endsWith(shortLink.redirectLink));
    }

    @Test
    @DisplayName("Should redirect to default url without short link")
    public void shouldDefaultRedirectWithoutShortLinkTest() {
        Response response = given()
                .redirects()
                .follow(false)
                .expect()
                .statusCode(303)
                .when()
                .get(URL);

        assertEquals(defaultHost, response.getHeader("Location"));
    }
}