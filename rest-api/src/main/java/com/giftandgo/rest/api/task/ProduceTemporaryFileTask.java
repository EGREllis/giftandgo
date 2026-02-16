package com.giftandgo.rest.api.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

class ProduceTemporaryFileTask implements Runnable {
    private final UUID requestId;
    private final byte[] data;

    ProduceTemporaryFileTask(UUID requestId, byte[] data) {
        this.requestId = requestId;
        this.data = data;
    }

    @Override
    public void run() {
        produceTemporaryFile(requestId, data);
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
}
