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

import io.quarkus.logging.Log;

import javax.enterprise.context.ApplicationScoped;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@ApplicationScoped
public class LinkGenerator {
    private final SecureRandom secureRandom;

    public LinkGenerator() {
        SecureRandom secureRandom;
        try {
            secureRandom = SecureRandom.getInstance("NativePRNGNonBlocking");
        } catch (NoSuchAlgorithmException ex) {
            Log.warn("Can't use NativePRNGNonBlocking algorithm: " + ex);
            secureRandom = new SecureRandom();
        }
        this.secureRandom = secureRandom;
    }

    public String generateShortLink(int passLength) {
        byte[] arr = new byte[passLength];
        secureRandom.nextBytes(arr);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(arr);
    }
}
