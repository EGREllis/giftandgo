package com.giftandgo.rest.api.utils;

import com.giftandgo.rest.api.model.RequestInputLine;
import com.giftandgo.rest.api.model.RequestOutputLine;

import java.math.BigDecimal;
import java.util.UUID;

public class TestUtils {
    public static RequestInputLine buildRequestInputLine() {
        return new RequestInputLine(
                UUID.randomUUID(),
                "id",
                "name",
                "likes",
                "transport",
                new BigDecimal(1.0),
                new BigDecimal(3.0)
        );
    }

    public static RequestOutputLine buildRequestOutputLine() {
        return new RequestOutputLine(
                "name output",
                "transport output",
                new BigDecimal(5.2)
        );
    }
}
