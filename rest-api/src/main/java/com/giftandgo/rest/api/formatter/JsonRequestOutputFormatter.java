package com.giftandgo.rest.api.formatter;

import com.giftandgo.rest.api.model.RequestOutputLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.StringWriter;
import java.util.List;

@Component
public class JsonRequestOutputFormatter implements Formatter<List<RequestOutputLine>> {
    @Autowired
    private JsonMapper mapper;

    public JsonRequestOutputFormatter(JsonMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String format(List<RequestOutputLine> data) {
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, data);
        return writer.toString();
    }
}
