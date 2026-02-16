package com.giftandgo.rest.api.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RequestStart(UUID requestId, String requestUrl, OffsetDateTime timestamp, long startMillis) {
}
