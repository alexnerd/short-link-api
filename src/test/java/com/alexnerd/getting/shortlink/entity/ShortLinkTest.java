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

package com.alexnerd.getting.shortlink.entity;

import com.alexnerd.shortlink.buisness.entity.ShortLink;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.PersistenceException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@TestTransaction
public class ShortLinkTest {

    private final String fullUrl = "https://alexnerd.com";

    private final String shortUrl = "short_url";

    @Test
    @DisplayName("Should successful save short link")
    public void shouldSaveShortLinkTest() {
        ShortLink shortLink = new ShortLink();
        shortLink.shortLink = this.shortUrl;
        shortLink.redirectLink = this.fullUrl;
        shortLink.hash = this.fullUrl.hashCode();
        shortLink.createTime = LocalDateTime.now();
        shortLink.persist();

        assertTrue(ShortLink.findByIdOptional(shortLink.shortLink).isPresent());
    }

    @Test
    @DisplayName("Should not save same short link")
    public void shouldNotSaveSameShortLinkTest() {
        ShortLink shortLink = new ShortLink();
        shortLink.shortLink = this.shortUrl;
        shortLink.redirectLink = this.fullUrl;
        shortLink.hash = this.fullUrl.hashCode();
        shortLink.createTime = LocalDateTime.now();
        shortLink.persist();

        assertTrue(ShortLink.findByIdOptional(shortLink.shortLink).isPresent());

        ShortLink duplicateLink = new ShortLink();
        duplicateLink.shortLink = this.shortUrl;
        duplicateLink.redirectLink = this.fullUrl + "unique";
        duplicateLink.hash = (this.fullUrl + "unique").hashCode();
        duplicateLink.createTime = LocalDateTime.now();

        assertThrows(PersistenceException.class, duplicateLink::persist);
    }

    @Test
    @DisplayName("Should not save same full link")
    public void shouldNotSaveSameFullLinkTest() {
        ShortLink shortLink = new ShortLink();
        shortLink.shortLink = this.shortUrl;
        shortLink.redirectLink = this.fullUrl;
        shortLink.hash = this.fullUrl.hashCode();
        shortLink.createTime = LocalDateTime.now();
        shortLink.persist();

        assertTrue(ShortLink.findByIdOptional(shortLink.shortLink).isPresent());

        ShortLink duplicateLink = new ShortLink();
        duplicateLink.shortLink = this.shortUrl + "unique";
        duplicateLink.redirectLink = this.fullUrl;
        duplicateLink.hash = this.fullUrl.hashCode();
        duplicateLink.createTime = LocalDateTime.now();

        assertThrows(PersistenceException.class, duplicateLink::persistAndFlush);
    }
}
