package com.giftandgo.rest.api.validator.blacklist;

import com.giftandgo.rest.api.validator.ValidationRecord;

import java.util.Optional;

public interface BlackList {
    Optional<String> excludeFromProcessing(ValidationRecord validationRecord);
}
