package com.giftandgo.rest.api.validator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class JsonValidationParser implements ValidationParser {
    @Autowired
    private JsonMapper mapper;

    public JsonValidationParser(JsonMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public ValidationRecord parse(String input) {
        return mapper.readValue(input, ValidationRecord.class);
    }
}
