package com.giftandgo.rest.api.parser;

import com.giftandgo.rest.api.model.RequestInputLine;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class CsvLineParser implements LineParser {

    @Override
    public RequestInputLine parse(String line) {
        String[] fields = line.split("[|]");
        if (fields.length != 7) {
            throw new IllegalArgumentException("Exception: line does not match expected pattern.  Line was:\n"+line);
        }
        UUID uuid = UUID.fromString(fields[0]);
        String id = fields[1];
        String name = fields[2];
        String likes = fields[3];
        String transport = fields[4];
        BigDecimal avgSpeed = new BigDecimal(fields[5]);
        BigDecimal topSpeed = new BigDecimal(fields[6]);
        return new RequestInputLine(
                uuid, id, name, likes, transport, avgSpeed, topSpeed);
    }
}
