package com.giftandgo.rest.api.parser;

import com.giftandgo.rest.api.model.RequestInputLine;

import java.util.List;

public interface RequestParser {
    List<RequestInputLine> parse(byte[] data);
}
