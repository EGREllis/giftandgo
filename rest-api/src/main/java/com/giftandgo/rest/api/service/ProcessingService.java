package com.giftandgo.rest.api.service;

import com.giftandgo.rest.api.model.RequestStart;
import com.giftandgo.rest.api.task.TaskFactory;
import com.giftandgo.rest.api.validator.ValidationRecord;
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
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Slf4j
@Component
public class ProcessingService {
    @Autowired
    private BlackList blackList;

    @Autowired
    private Clock clock;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private TaskFactory taskFactory;

    @Value("${feature.flag.validation.enabled}")
    private boolean isValidating;

    public ResponseEntity<Resource> process(UUID requestId, MultipartFile input, HttpServletRequest request) {
        RequestStart start = new RequestStart(requestId, request.getRequestURI(), OffsetDateTime.now(clock), System.currentTimeMillis());

        // Optimistically process the data.
        Callable<byte[]> dataProcessing = taskFactory.processDataTask(input);
        Future<byte[]> myProcessedData = executorService.submit(dataProcessing);

        // Fetch the validation data from ip-api concurrently - doesn't really save much time as we wait for it immediately
        String ipAddress = fetchIpAddress(request);
        Callable<ValidationRecord> validationTask = taskFactory.fetchValidationRecord(ipAddress);
        Future<ValidationRecord> myValidationRecord = executorService.submit(validationTask);

        // Decided what to do.
        ValidationRecord validationRecord = get(myValidationRecord);
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
        byte[] processedData = get(myProcessedData);
        executorService.submit(taskFactory.createTemporaryFile(requestId, processedData));
        return packageDataForReturnToClient(processedData, ipAddress, validationRecord, start);
    }

    private static <T> T get(Future<T> future) {
        T result;
        try {
            result = future.get();
        } catch(InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private ResponseEntity<Resource> packageDataForReturnToClient(byte[] data, String ipAddress, ValidationRecord record, RequestStart start) {
        logRequestToDatabase(200, ipAddress, record, start);
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(data));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"OutcomeFile.json\"")
                .body(resource);
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
        executorService.submit(taskFactory.persistRequestToDatabase(statusCode, ipAddress, record, start, endMillis));
    }
}
