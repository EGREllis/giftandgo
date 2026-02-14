package com.giftandgo.rest.api.converter;

import com.giftandgo.rest.api.model.RequestInputLine;
import com.giftandgo.rest.api.model.RequestOutputLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.giftandgo.rest.api.utils.TestUtils.buildRequestInputLine;
import static org.assertj.core.api.Assertions.assertThat;

class SimpleRequestConverterTests {
    private Converter<RequestInputLine, RequestOutputLine> converter;

    @BeforeEach
    public void setup() {
        converter = new SimpleRequestConverter();
    }

    @Test
    void verifyHappyPath() {
        RequestInputLine inputLine = buildRequestInputLine();

        RequestOutputLine actual = converter.convert(inputLine);

        assertThat(actual.name()).isEqualTo(inputLine.name());
        assertThat(actual.transport()).isEqualTo(inputLine.transport());
        assertThat(actual.topSpeed()).isEqualTo(inputLine.topSpeed());
    }
}
