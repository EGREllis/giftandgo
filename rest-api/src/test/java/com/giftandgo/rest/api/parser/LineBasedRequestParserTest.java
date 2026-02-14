package com.giftandgo.rest.api.parser;

import com.giftandgo.rest.api.model.RequestInputLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.giftandgo.rest.api.utils.TestUtils.buildRequestInputLine;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LineBasedRequestParserTest {
    private LineParser lineParser;
    private RequestParser requestParser;

    @BeforeEach
    public void setup() {
        lineParser = mock(LineParser.class);
        requestParser = new LineBasedRequestParser(lineParser);
    }

    @Test
    void verifyLineBasedRequestParserWorksWithOneLine() {
        String fullLine = "1afb6f5d-a7c2-4311-a92d-974f3180ff5e|3X3D35|Jenny Walters|Likes Avocados|Rides A Scooter|8.5|15.3\n";
        String dataLine = "1afb6f5d-a7c2-4311-a92d-974f3180ff5e|3X3D35|Jenny Walters|Likes Avocados|Rides A Scooter|8.5|15.3";
        byte[] data = fullLine.getBytes();
        RequestInputLine requestInputLine = buildRequestInputLine();

        when(lineParser.parse(eq(dataLine))).thenReturn(requestInputLine);

        List<RequestInputLine> inputLines = requestParser.parse(data);

        assertThat(inputLines.size()).isEqualTo(1);
        assertThat(inputLines.get(0)).isSameAs(requestInputLine);
    }
}
