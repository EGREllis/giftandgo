package com.giftandgo.rest.api.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Request(UUID requestId, String requestUrl, OffsetDateTime timestamp,
        int responseCode, String ipAddress, String countryCode, String isp, long timeLapsed) {
}
