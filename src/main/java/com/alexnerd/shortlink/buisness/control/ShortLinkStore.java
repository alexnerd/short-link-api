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

package com.alexnerd.shortlink.buisness.control;

import com.alexnerd.shortlink.buisness.entity.ShortLink;
import io.quarkus.logging.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@ApplicationScoped
public class ShortLinkStore {

    @ConfigProperty(name = "application.host")
    String defaultHost;

    @ConfigProperty(name = "default.retry-num")
    int retryNum;

    @ConfigProperty(name = "default.link-length")
    int linkLength;

    private final SecureRandom secureRandom;

    public ShortLinkStore() {
        SecureRandom secureRandom;
        try {
            secureRandom = SecureRandom.getInstance("NativePRNGNonBlocking");
        } catch (NoSuchAlgorithmException ex) {
            Log.warn("Can't use NativePRNGNonBlocking algorithm: " + ex);
            secureRandom = new SecureRandom();
        }
        this.secureRandom = secureRandom;
    }

    @Transactional
    public String getRedirectLink(String shortLink) {
        return ShortLink.find("shortLink", shortLink)
                .singleResultOptional()
                .map(s -> (ShortLink) s)
                .map(this::updateReadTime)
                .map(s -> s.redirectLink)
                .orElse(defaultHost);
    }

    public ShortLink getShortLink(ShortLink shortLink) {
        return this.getShortLink(shortLink.redirectLink);
    }

    @Transactional
    public ShortLink getShortLink(String redirectLink) {
        return ShortLink.find("hash", redirectLink.hashCode())
                .singleResultOptional()
                .map(s -> (ShortLink) s)
                .orElseGet(() -> this.createShortLink(redirectLink));
    }

    private ShortLink updateReadTime(ShortLink shortLink) {
        shortLink.readTime = LocalDateTime.now();
        return shortLink;
    }

    private ShortLink createShortLink(String redirectLink) {
        ShortLink shortLink = new ShortLink();
        shortLink.redirectLink = redirectLink;
        shortLink.hash = redirectLink.hashCode();
        shortLink.createTime = LocalDateTime.now();
        for (int i = 0; i < retryNum; i++) {
            try {
                shortLink.shortLink = generateShortLink(linkLength);
                shortLink.persist();
                return shortLink;
            } catch (Exception ex) {
                Log.warn("Can't create ShortLink object" + ex);
            }
        }
        throw new ShortLinkException("Can't generate short link for " + redirectLink, 400);
    }

    private String generateShortLink(int passLength) {
        byte[] arr = new byte[passLength];
        secureRandom.nextBytes(arr);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(arr);
    }
}