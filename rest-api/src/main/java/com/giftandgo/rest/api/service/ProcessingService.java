package com.giftandgo.rest.api.service;

import com.giftandgo.rest.api.converter.Converter;
import com.giftandgo.rest.api.formatter.Formatter;
import com.giftandgo.rest.api.model.RequestInputLine;
import com.giftandgo.rest.api.model.RequestOutputLine;
import com.giftandgo.rest.api.parser.RequestParser;
import com.giftandgo.rest.api.validator.ValidationRecord;
import com.giftandgo.rest.api.validator.Validator;
import com.giftandgo.rest.api.validator.blacklist.BlackList;
import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
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
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class ProcessingService {
    @Autowired
    private RequestParser requestParser;

    @Autowired
    private Converter<RequestInputLine, RequestOutputLine> requestConverter;

    @Autowired
    private Formatter<List<RequestOutputLine>> formatter;

    @Autowired
    private Validator validator;

    @Autowired
    private BlackList blackList;

    @Value("${feature.flag.validation.enabled}")
    private boolean isValidating;

    public ResponseEntity<Resource> process(UUID requestId, MultipartFile input, HttpServletRequest request) {
        byte[] data = fetchData(requestId, input);
        byte[] processedData = processData(data);       // This could be written in a lot less code, but you wanted to see SOLID principles.
        ValidationRecord validationRecord = getValidationRecord(request);
        if (isFailure(validationRecord)) {
            return rejection("Validation failure.");
        } else {
            Optional<String> blackListedMessage = blackList.excludeFromProcessing(validationRecord);
            if (blackListedMessage.isPresent()) {
                return rejection(blackListedMessage.get());
            }
        }
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

    private ResponseEntity<Resource> packageDataForReturnToClient(UUID requestId, byte[] data) {
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

    private String fetchIpAddress(HttpServletRequest request) {
        String ipAddress;
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor == null) {
            ipAddress = request.getRemoteAddr().trim();
        } else {
            ipAddress = forwardedFor.split(",")[0].trim();
        }
        return ipAddress;
    }

    private ValidationRecord getValidationRecord(HttpServletRequest request) {
        String ipAddress = fetchIpAddress(request);
        return validator.validate(ipAddress);
    }

    private boolean isFailure(ValidationRecord record) {
        return "fail".equalsIgnoreCase(record.status());
    }

    private ResponseEntity<Resource> rejection(String message) {
        return ResponseEntity.status(403).body(new InputStreamResource(new ByteArrayInputStream(message.getBytes())));
    }
}
