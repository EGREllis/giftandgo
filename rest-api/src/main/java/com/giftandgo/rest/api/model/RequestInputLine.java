package com.giftandgo.rest.api.model;

import java.math.BigDecimal;
import java.util.UUID;

public record RequestInputLine(UUID uuid, String id, String name, String Likes, String transport, BigDecimal avgSpeed, BigDecimal topSpeed) {
}
