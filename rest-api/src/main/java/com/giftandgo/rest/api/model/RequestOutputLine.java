package com.giftandgo.rest.api.model;

import java.math.BigDecimal;

public record RequestOutputLine(String name, String transport, BigDecimal topSpeed) {
}
