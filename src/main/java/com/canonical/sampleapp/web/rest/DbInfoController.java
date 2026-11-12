/*
* Copyright 2025 Canonical Ltd.
* See LICENSE file for licensing details.
*/

package com.canonical.sampleapp.web.rest;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DbInfoController {
    private final Logger log = LoggerFactory.getLogger(DbInfoController.class);

    private final DataSource dataSource;

    public DbInfoController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping(value = "/db/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> dbInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            var meta = connection.getMetaData();
            info.put("productName", meta.getDatabaseProductName());
            info.put("productVersion", meta.getDatabaseProductVersion());
            info.put("driver", meta.getDriverName());
            info.put("url", meta.getURL());
            return ResponseEntity.ok(info);
        } catch (SQLException e) {
            log.error("Could not read database metadata", e);
            info.put("error", "Could not read database metadata");
            return new ResponseEntity<>(info, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
