package com.giftandgo.rest.api.task;

import com.giftandgo.rest.api.converter.Converter;
import com.giftandgo.rest.api.formatter.Formatter;
import com.giftandgo.rest.api.model.RequestInputLine;
import com.giftandgo.rest.api.model.RequestOutputLine;
import com.giftandgo.rest.api.model.RequestStart;
import com.giftandgo.rest.api.parser.RequestParser;
import com.giftandgo.rest.api.repository.RequestRepository;
import com.giftandgo.rest.api.validator.ValidationRecord;
import com.giftandgo.rest.api.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

@Component
public class StandardTaskFactory implements TaskFactory {
    @Autowired
    private RequestParser requestParser;

    @Autowired
    private Converter<RequestInputLine, RequestOutputLine> requestConverter;

    @Autowired
    private Formatter<List<RequestOutputLine>> formatter;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private Validator validator;

    @Override
    public Callable<byte[]> processDataTask(MultipartFile multipartFile) {
        return new ProcessDataTask(multipartFile, requestParser, requestConverter, formatter);
    }

    @Override
    public Callable<ValidationRecord> fetchValidationRecord(String ipAddress) {
        return new FetchValidationRecordTask(validator, ipAddress);
    }

    @Override
    public Runnable createTemporaryFile(UUID requestId, byte[] data) {
        return new ProduceTemporaryFileTask(requestId, data);
    }

    @Override
    public Runnable persistRequestToDatabase(int statusCode, String ipAddress, ValidationRecord validationRecord, RequestStart start, long requestEndMillis) {
        return new PersistRequestToDatabaseTask(requestRepository, statusCode, ipAddress, validationRecord, start, requestEndMillis);
    }
}
