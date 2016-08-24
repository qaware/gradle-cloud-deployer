package de.qaware.cloud.deployer.kubernetes;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileUtil {

    private FileUtil() {
    }

    public static String readFile(File file) throws IOException {
        return FileUtils.readFileToString(file, Charset.defaultCharset());
    }

    public static String readFile(String filename) throws IOException {
        File file = new File(FileUtil.class.getClass().getResource(filename).getPath());
        return readFile(file);
    }
}
