package com.giftandgo.rest.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

@Slf4j
@RestController
public class ProcessingController {

    @RequestMapping(value="/process", method= RequestMethod.POST)
    public ResponseEntity process(@RequestParam("input")MultipartFile input, HttpServletRequest request) {
        UUID requestId = UUID.randomUUID();
        log.info("Processing new input file trace: {}", requestId);
        File tempFile;
        try {
            tempFile = File.createTempFile(requestId.toString(), ".txt");
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        byte[] data;
        try (FileOutputStream tempOutput = new FileOutputStream(tempFile)) {
            data = input.getBytes();
            log.info("File {} size {}", requestId, data.length);
            tempOutput.write(data);
            tempOutput.flush();
        } catch(IOException ioe) {
            throw new RuntimeException(ioe);
        }
        log.info("Processed input file trace: {} at {}", requestId, tempFile.getAbsolutePath());

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(data));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"OutcomeFile.json\"")
                .body(resource);
    }
}
