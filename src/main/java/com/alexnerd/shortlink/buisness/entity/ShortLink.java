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

package com.alexnerd.shortlink.buisness.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "short_link")
public class ShortLink extends PanacheEntityBase {
    @Id
    @Column(name = "short_link")
    public String shortLink;

    @NotBlank
    @Column(name = "redirect_link", unique = true, nullable = false)
    public String redirectLink;

    @Column(name = "hash", unique = true, nullable = false)
    public Integer hash;

    @Column(name = "create_time", nullable = false, updatable = false)
    public LocalDateTime createTime;

    @Column(name = "read_time")
    public LocalDateTime readTime;

}
