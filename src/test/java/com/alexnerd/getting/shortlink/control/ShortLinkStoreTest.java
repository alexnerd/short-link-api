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

package com.alexnerd.getting.shortlink.control;

import com.alexnerd.shortlink.buisness.control.ShortLinkStore;
import com.alexnerd.shortlink.buisness.entity.ShortLink;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestTransaction
public class ShortLinkStoreTest {

    @Inject
    ShortLinkStore store;

    @ConfigProperty(name = "application.host")
    String defaultHost;

    @Test
    @DisplayName("Should create short link")
    public void shouldCreateShortLinkTest() {
        String redirectLink = "https://test-link.ru";
        ShortLink shortLink = store.getShortLink(redirectLink);

        assertNotNull(shortLink.createTime);
        assertNull(shortLink.readTime);
        assertEquals(redirectLink, shortLink.redirectLink);
    }

    @Test
    @DisplayName("Should not create short link if redirect link already exists")
    public void shouldNotCreateIfRedirectLinkExistsTest() {
        String redirectLink = "https://redirect-link.ru";
        ShortLink shortLinkFirst = store.getShortLink(redirectLink);
        ShortLink shortLinkSecond = store.getShortLink(redirectLink);

        assertEquals(shortLinkFirst.shortLink, shortLinkSecond.shortLink);
    }

    @Test
    @DisplayName("Should create different link for different redirect link")
    public void shouldCreateDifferentLinkTest() {
        String redirectLinkFirst = "https://test-link-1.ru";
        String redirectLinkSecond = "https://test-link-2.ru";
        ShortLink shortLinkFirst = store.getShortLink(redirectLinkFirst);
        ShortLink shortLinkSecond = store.getShortLink(redirectLinkSecond);

        assertNotEquals(shortLinkFirst.shortLink, shortLinkSecond.shortLink);
    }

    @Test
    @DisplayName("Should return default link if short link not exist")
    public void shouldReturnDefaultLinkTest() {
        String shortLink = "test_short_link";
        String redirectLink = store.getRedirectLink(shortLink);

        assertEquals(defaultHost, redirectLink);
    }

    @Test
    @DisplayName("Should update read time after redirect")
    public void shouldUpdateReadTimeTest() {
        String redirectLink = "https://test-link.ru";
        ShortLink shortLink = store.getShortLink(redirectLink);

        assertNull(shortLink.readTime);

        store.getRedirectLink(shortLink.shortLink);
        ShortLink shortLinkFromDb = ShortLink.findById(shortLink.shortLink);

        assertNotNull(shortLinkFromDb.readTime);
    }
}
