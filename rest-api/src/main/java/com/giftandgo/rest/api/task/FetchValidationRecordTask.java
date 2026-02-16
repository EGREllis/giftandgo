package com.giftandgo.rest.api.task;

import com.giftandgo.rest.api.validator.ValidationRecord;
import com.giftandgo.rest.api.validator.Validator;

import java.util.concurrent.Callable;

class FetchValidationRecordTask implements Callable<ValidationRecord> {
    private final Validator validator;
    private final String ipAddress;

    FetchValidationRecordTask(Validator validator, String ipAddress) {
        this.validator = validator;
        this.ipAddress = ipAddress;
    }

    @Override
    public ValidationRecord call() {
        return validator.validate(ipAddress);
    }
}
