package de.qaware.cloud.deployer.commons.config.resource;

import de.qaware.cloud.deployer.commons.error.ResourceConfigException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public abstract class BaseResourceConfigFactory<T extends BaseResourceConfig> {

    public abstract List<T> createConfigs(List<File> files) throws ResourceConfigException;

    protected ContentType retrieveContentType(File file) throws ResourceConfigException {
        String fileEnding = FilenameUtils.getExtension(file.getName());
        switch (fileEnding) {
            case "json":
                return ContentType.JSON;
            case "yml":
                return ContentType.YAML;
            default:
                throw new ResourceConfigException("Unsupported content type for file ending: " + fileEnding + "(File: " + file.getName() + ")");
        }
    }

    protected String readFileContent(File file) throws ResourceConfigException {
        try {
            return FileUtils.readFileToString(file, Charset.defaultCharset()).trim();
        } catch (IOException e) {
            throw new ResourceConfigException(e.getMessage(), e);
        }
    }
}
