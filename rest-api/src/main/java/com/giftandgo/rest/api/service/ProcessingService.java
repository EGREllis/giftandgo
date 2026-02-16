package com.giftandgo.rest.api.service;

import com.giftandgo.rest.api.converter.Converter;
import com.giftandgo.rest.api.formatter.Formatter;
import com.giftandgo.rest.api.model.Request;
import com.giftandgo.rest.api.model.RequestInputLine;
import com.giftandgo.rest.api.model.RequestOutputLine;
import com.giftandgo.rest.api.model.RequestStart;
import com.giftandgo.rest.api.parser.RequestParser;
import com.giftandgo.rest.api.repository.RequestRepository;
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
import java.time.Clock;
import java.time.OffsetDateTime;
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

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private Clock clock;

    @Value("${feature.flag.validation.enabled}")
    private boolean isValidating;

    public ResponseEntity<Resource> process(UUID requestId, MultipartFile input, HttpServletRequest request) {
        RequestStart start = new RequestStart(requestId, request.getRequestURI(), OffsetDateTime.now(clock), System.currentTimeMillis());

        // This could be done optimistically in parrallel with the validation checks.
        byte[] data = fetchData(input);
        byte[] processedData = processData(data);

        String ipAddress = fetchIpAddress(request);
        ValidationRecord validationRecord = getValidationRecord(ipAddress);
        if (isValidating) {
            if (isFailure(validationRecord)) {
                return rejection("Validation failure.", ipAddress, validationRecord, start);
            } else {
                Optional<String> blackListedMessage = blackList.excludeFromProcessing(validationRecord);
                if (blackListedMessage.isPresent()) {
                    return rejection(blackListedMessage.get(), ipAddress, validationRecord, start);
                }
            }
        }
        produceTemporaryFile(requestId, processedData); // This is un-necessary but it is in the spec, so here it is.
        return packageDataForReturnToClient(processedData, ipAddress, validationRecord, start);
    }

    private byte[] fetchData(MultipartFile multipartFile) {
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

    private ResponseEntity<Resource> packageDataForReturnToClient(byte[] data, String ipAddress, ValidationRecord record, RequestStart start) {
        logRequestToDatabase(200, ipAddress, record, start);
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

    private ValidationRecord getValidationRecord(String ipAddress) {
        return validator.validate(ipAddress);
    }

    private boolean isFailure(ValidationRecord record) {
        return "fail".equalsIgnoreCase(record.status());
    }

    private ResponseEntity<Resource> rejection(String message, String ipAddress, ValidationRecord record, RequestStart start) {
        int statusCode = 403;
        logRequestToDatabase(statusCode, ipAddress, record, start);
        return ResponseEntity.status(statusCode).body(new InputStreamResource(new ByteArrayInputStream(message.getBytes())));
    }

    private void logRequestToDatabase(int statusCode, String ipAddress, ValidationRecord record, RequestStart start) {
        long endMillis = System.currentTimeMillis();
        Request request = new Request(
                start.requestId(), start.requestUrl(), start.timestamp(),
                statusCode, ipAddress, record.countryCode(), record.isp(), endMillis - start.startMillis());
        requestRepository.insert(request);
    }
}
