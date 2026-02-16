package com.giftandgo.rest.api.task;

import com.giftandgo.rest.api.converter.Converter;
import com.giftandgo.rest.api.formatter.Formatter;
import com.giftandgo.rest.api.model.RequestInputLine;
import com.giftandgo.rest.api.model.RequestOutputLine;
import com.giftandgo.rest.api.parser.RequestParser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

class ProcessDataTask implements Callable<byte[]> {
    private final RequestParser requestParser;
    private final Converter<RequestInputLine, RequestOutputLine> requestConverter;
    private final Formatter<List<RequestOutputLine>> formatter;
    private final MultipartFile multipartFile;

    ProcessDataTask(MultipartFile multipartFile, RequestParser requestParser, Converter<RequestInputLine, RequestOutputLine> requestConverter,
                    Formatter<List<RequestOutputLine>> formatter) {
        this.multipartFile = multipartFile;
        this.requestParser = requestParser;
        this.requestConverter = requestConverter;
        this.formatter = formatter;
    }

    @Override
    public byte[] call() {
        byte[] rawData = fetchData(multipartFile);
        return processData(rawData);
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
}
