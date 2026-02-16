package com.giftandgo.rest.api.task;

import com.giftandgo.rest.api.model.Request;
import com.giftandgo.rest.api.model.RequestStart;
import com.giftandgo.rest.api.repository.RequestRepository;
import com.giftandgo.rest.api.validator.ValidationRecord;

class PersistRequestToDatabaseTask implements Runnable {
    private final RequestRepository requestRepository;
    private final int statusCode;
    private final String ipAddress;
    private final ValidationRecord validationRecord;
    private final RequestStart start;
    private final long requestEndMillis;

    PersistRequestToDatabaseTask(RequestRepository requestRepository, int statusCode, String ipAddress, ValidationRecord validationRecord,
                                 RequestStart start, long requestEndMillis) {
        this.requestRepository = requestRepository;
        this.statusCode = statusCode;
        this.ipAddress = ipAddress;
        this.validationRecord = validationRecord;
        this.start = start;
        this.requestEndMillis = requestEndMillis;
    }

    @Override
    public void run() {
        logRequestToDatabase(statusCode, ipAddress, validationRecord, start);
    }

    private void logRequestToDatabase(int statusCode, String ipAddress, ValidationRecord record, RequestStart start) {
        Request request = new Request(
                start.requestId(), start.requestUrl(), start.timestamp(),
                statusCode, ipAddress, record.countryCode(), record.isp(), requestEndMillis - start.startMillis());
        requestRepository.insert(request);
    }
}
