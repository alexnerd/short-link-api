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

package com.alexnerd.shortlink.buisness.boundary;

import com.alexnerd.shortlink.buisness.entity.ShortLink;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@ApplicationScoped
public class JobScheduler {

    @ConfigProperty(name = "default.job-scheduler.month")
    int month;

    // 00:00 every day
    @Transactional
    @Scheduled(cron = "0 0 * * * ?")
    void cleanDB() {
        Log.info("Start JobScheduler clean DB");
        long deleted = ShortLink.delete("readTime < ?1 or (readTime = NULL and createTime < ?1)",
                LocalDateTime.now().minusMonths(month));
        Log.info("Finish JobScheduler clean DB, deleted rows: " + deleted);
    }

}
