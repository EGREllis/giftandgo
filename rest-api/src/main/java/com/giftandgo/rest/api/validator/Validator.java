package com.giftandgo.rest.api.validator;

import java.util.Optional;

public interface Validator {
    Optional<String> getInvalidMessage(String ipAddress);
}
