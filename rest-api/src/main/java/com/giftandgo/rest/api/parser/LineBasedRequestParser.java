package com.giftandgo.rest.api.parser;

import com.giftandgo.rest.api.model.RequestInputLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LineBasedRequestParser implements RequestParser {
    @Autowired
    private LineParser lineParser;

    public LineBasedRequestParser(LineParser lineParser) {
        this.lineParser = lineParser;
    }

    @Override
    public List<RequestInputLine> parse(byte[] data) {
        List<RequestInputLine> results = new ArrayList<>();
        for (String line : new String(data).split("\n")) {
            RequestInputLine inputLine = lineParser.parse(line);
            results.add(inputLine);
        }
        return results;
    }
}
