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

CREATE TABLE short_link
(
    short_link      VARCHAR       PRIMARY KEY,
    redirect_link   VARCHAR       NOT NULL UNIQUE,
    hash            INTEGER       NOT NULL UNIQUE,
    read_time       TIMESTAMP     DEFAULT NULL,
    create_time     TIMESTAMP     NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX short_link_hash_idx ON short_link (hash);

COMMENT ON TABLE    short_link                  IS 'Table with short links';
COMMENT ON COLUMN   short_link.short_link       IS 'Primary key short link';
COMMENT ON COLUMN   short_link.redirect_link    IS 'Redirect link';
COMMENT ON COLUMN   short_link.hash             IS 'Redirect link hash';
COMMENT ON COLUMN   short_link.read_time        IS 'Last redirect time';
COMMENT ON COLUMN   short_link.create_time      IS 'Short link create time';