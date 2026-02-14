package com.giftandgo.rest.api.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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
import java.util.UUID;

@Component
@Slf4j
public class ProcessingService {
    public ResponseEntity process(UUID requestId, MultipartFile input, HttpServletRequest request) {
        byte[] data = fetchData(requestId, input);
        produceTemporaryFile(requestId, data);
        return packageDataForReturnToClient(requestId, data);
    }

    private byte[] fetchData(UUID requestId, MultipartFile multipartFile) {
        byte[] data;
        try {
            data = multipartFile.getBytes();
            log.info("Trace {} request size is {}", requestId, data.length);
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
            log.info("Trace {} wrote temporary file at {}", requestId, tempFile.getAbsolutePath());
        } catch(IOException ioe) {
            throw new RuntimeException("Exception writing to temp file.", ioe);
        }
    }

    private ResponseEntity packageDataForReturnToClient(UUID requestId, byte[] data) {
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(data));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"OutcomeFile.json\"")
                .body(resource);
    }
}
