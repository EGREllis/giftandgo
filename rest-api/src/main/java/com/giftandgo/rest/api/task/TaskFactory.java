package com.giftandgo.rest.api.task;

import com.giftandgo.rest.api.model.RequestStart;
import com.giftandgo.rest.api.validator.ValidationRecord;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.Callable;

public interface TaskFactory {
    Callable<byte[]> processDataTask(MultipartFile multipartFile);
    Callable<ValidationRecord> fetchValidationRecord(String ipAddress);
    Runnable createTemporaryFile(UUID requestId, byte[] data);
    Runnable persistRequestToDatabase(int statusCode, String ipAddress, ValidationRecord validationRecord,
                                      RequestStart start, long requestEndMillis);
}
