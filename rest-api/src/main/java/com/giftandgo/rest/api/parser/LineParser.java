package com.giftandgo.rest.api.parser;

import com.giftandgo.rest.api.model.RequestInputLine;

public interface LineParser {
    RequestInputLine parse(String line);
}
