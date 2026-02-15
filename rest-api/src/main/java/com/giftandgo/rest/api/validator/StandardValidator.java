package com.giftandgo.rest.api.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StandardValidator implements Validator {
    @Autowired
    private Source<String> ipApiSource;

    @Autowired
    private ValidationParser validationParser;

    @Override
    public ValidationRecord validate(String ipAddress) {
        String rawResponse = ipApiSource.load(ipAddress);
        return validationParser.parse(rawResponse);
    }
}
