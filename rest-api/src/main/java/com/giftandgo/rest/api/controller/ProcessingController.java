package com.giftandgo.rest.api.controller;

import com.giftandgo.rest.api.service.ProcessingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private ProcessingService processingService;

    @RequestMapping(value="/process", method= RequestMethod.POST)
    public ResponseEntity process(@RequestParam("input")MultipartFile input, HttpServletRequest request) {
        UUID requestId = UUID.randomUUID();
        log.info("Processing new input file trace: {}", requestId);
        return processingService.process(requestId, input, request);
    }
}
