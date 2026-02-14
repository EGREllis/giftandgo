package com.giftandgo.rest.api.parser;

import com.giftandgo.rest.api.model.RequestInputLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
        try (BufferedReader lineReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)))) {
            String line;
            while ( (line = lineReader.readLine()) != null ) {
                RequestInputLine inputLine = lineParser.parse(line);
                results.add(inputLine);
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Exception while parsing inputs.", ioe);
        }
        return results;
    }
}
