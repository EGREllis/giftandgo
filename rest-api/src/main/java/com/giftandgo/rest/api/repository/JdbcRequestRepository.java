package com.giftandgo.rest.api.repository;

import com.giftandgo.rest.api.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcRequestRepository implements RequestRepository {
    private static final String INSERT_SQL =
            """
            INSERT INTO requests (
                uuid,
                url,
                request_time,
                response_code,
                ip_address,
                country_code,
                isp,
                time_lapsed
            ) VALUES (
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?
            );
            """;
    @Autowired
    private JdbcTemplate template;

    @Override
    public void insert(Request request) {
        template.update(INSERT_SQL,
                request.requestId(),
                request.requestUrl(),
                request.timestamp(),
                request.responseCode(),
                request.ipAddress(),
                request.countryCode(),
                request.isp(),
                request.timeLapsed());
    }
}
