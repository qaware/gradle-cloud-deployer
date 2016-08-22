package de.qaware.cloud.deployer.kubernetes.config.resource;

import java.io.IOException;

public class FileResourceConfig extends ResourceConfig {

    private final String filename;

    public FileResourceConfig(String filename, ContentType contentType, String content) throws IOException {
        super(contentType, content);
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public String toString() {
        return super.toString() + " (File: " + filename + ")";
    }
}
