package com.giftandgo.rest.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class ProcessingController {

    @RequestMapping(value="/process", method= RequestMethod.POST)
    public String process(@RequestParam("input")MultipartFile input) {
        log.info("Processing new input file.");
        log.info("Processed input file.");
        return "Processed";
    }
}
