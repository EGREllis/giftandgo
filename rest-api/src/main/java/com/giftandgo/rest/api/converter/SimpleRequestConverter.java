package com.giftandgo.rest.api.converter;

import com.giftandgo.rest.api.model.RequestInputLine;
import com.giftandgo.rest.api.model.RequestOutputLine;
import org.springframework.stereotype.Component;

@Component
public class SimpleRequestConverter implements Converter<RequestInputLine, RequestOutputLine> {
    @Override
    public RequestOutputLine convert(RequestInputLine input) {
        return new RequestOutputLine(input.name(), input.transport(), input.topSpeed());
    }
}
