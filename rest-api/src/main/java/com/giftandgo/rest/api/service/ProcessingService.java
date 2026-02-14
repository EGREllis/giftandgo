package com.giftandgo.rest.api.service;

import com.giftandgo.rest.api.converter.Converter;
import com.giftandgo.rest.api.formatter.Formatter;
import com.giftandgo.rest.api.model.RequestInputLine;
import com.giftandgo.rest.api.model.RequestOutputLine;
import com.giftandgo.rest.api.parser.RequestParser;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ProcessingService {
    @Autowired
    private RequestParser requestParser;

    @Autowired
    private Converter<RequestInputLine, RequestOutputLine> requestConverter;

    @Autowired
    private Formatter<List<RequestOutputLine>> formatter;

    public ResponseEntity process(UUID requestId, MultipartFile input, HttpServletRequest request) {
        byte[] data = fetchData(requestId, input);
        byte[] processedData = processData(data);       // This could be written in a lot less code, but you wanted to see SOLID principles.
        produceTemporaryFile(requestId, processedData); // This is un-necessary but it is in the spec, so here it is.
        return packageDataForReturnToClient(requestId, processedData);
    }

    private byte[] fetchData(UUID requestId, MultipartFile multipartFile) {
        byte[] data;
        try {
            data = multipartFile.getBytes();
        } catch (IOException ioe) {
            throw new RuntimeException("Exception while fetching input data.", ioe);
        }
        return data;
    }

    private void produceTemporaryFile(UUID requestId, byte[] data) {
        File tempFile;
        try {
            tempFile = File.createTempFile(requestId.toString(), ".txt");
        } catch (IOException ioe) {
            throw new RuntimeException("Exception creating temp file.", ioe);
        }
        try (FileOutputStream tempOutput = new FileOutputStream(tempFile)) {
            tempOutput.write(data);
            tempOutput.flush();
        } catch(IOException ioe) {
            throw new RuntimeException("Exception writing to temp file.", ioe);
        }
    }

    private ResponseEntity packageDataForReturnToClient(UUID requestId, byte[] data) {
        // This step could be removed (Springboot is clever enough to JSONify classes returned as a result from REST en-points)
        // However the spec says to transfer it as a file.
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(data));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"OutcomeFile.json\"")
                .body(resource);
    }

    private byte[] processData(byte[] data) {
        List<RequestInputLine> inputLines = requestParser.parse(data);
        List<RequestOutputLine> outputLines = new ArrayList<>(inputLines.size());
        for (RequestInputLine input : inputLines) {
            RequestOutputLine output = requestConverter.convert(input);
            outputLines.add(output);
        }
        String result = formatter.format(outputLines);
        return result.getBytes();
    }
}
