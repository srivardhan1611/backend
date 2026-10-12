package com.hostelfinder.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Hibernate's ddl-auto=update never widens an existing column, so older databases keep
 *  colleges.address at VARCHAR(255). Long addresses then fail with "Something went wrong". */
@Component
@Order(0)
@RequiredArgsConstructor
public class SchemaFix implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(SchemaFix.class);
    private final JdbcTemplate jdbc;

    @Override
    public void run(ApplicationArguments args) {
        // Leftover column 'ai_generated' (NOT NULL, no default) from an older schema breaks every insert.
        // Give it a default so the app can insert rows; the data is kept.
        try {
            var cols = jdbc.queryForList(
                "SELECT TABLE_NAME FROM information_schema.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() AND COLUMN_NAME = 'ai_generated'", String.class);
            for (String table : cols) {
                jdbc.execute("ALTER TABLE `" + table + "` ALTER COLUMN `ai_generated` SET DEFAULT 0");
                log.info("Set default 0 on {}.ai_generated", table);
            }
        } catch (Exception e) {
            log.warn("Could not fix ai_generated default: {}", e.getMessage());
        }
        try {
            jdbc.execute("ALTER TABLE colleges MODIFY address VARCHAR(500)");
            log.info("colleges.address widened to VARCHAR(500)");
        } catch (Exception e) {
            log.warn("Could not widen colleges.address (safe to ignore if already done): {}", e.getMessage());
        }
    }
}
