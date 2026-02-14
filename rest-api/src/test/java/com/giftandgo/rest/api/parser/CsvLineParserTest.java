package com.giftandgo.rest.api.parser;

import com.giftandgo.rest.api.model.RequestInputLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CsvLineParserTest {
    private LineParser lineParser;

    @BeforeEach
    public void setup() {
        lineParser = new CsvLineParser();
    }

    @Test
    void verifyParsesLineCorrectly() {
        String line = "1afb6f5d-a7c2-4311-a92d-974f3180ff5e|3X3D35|Jenny Walters|Likes Avocados|Rides A Scooter|8.5|15.3";
        RequestInputLine expectedResult = new RequestInputLine(
                UUID.fromString("1afb6f5d-a7c2-4311-a92d-974f3180ff5e"),
                "3X3D35",
                "Jenny Walters",
                "Likes Avocados",
                "Rides A Scooter",
                new BigDecimal("8.5"),
                new BigDecimal("15.3")
        );

        RequestInputLine actual = lineParser.parse(line);

        assertThat(actual).isEqualTo(expectedResult);
    }

    @Test
    void verifyExceptionThrownIfLineNotFormattedCorrectly() {
        String badInput = "";

        assertThatThrownBy(() -> lineParser.parse(badInput))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Exception: line does not match expected pattern.  Line was:\n");
    }
}
