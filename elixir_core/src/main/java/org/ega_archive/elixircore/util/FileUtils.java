package org.ega_archive.elixircore.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.log4j.Log4j;

@Log4j
public class FileUtils {
  
  public static String writeTemporaryFile(String xmlContent, String filename) throws IOException {
    if(StringUtils.isBlank(xmlContent)) {
      return null;
    }
    File tempFile = null;
    try {
      tempFile = File.createTempFile(filename, ".xml");
    } catch (IOException e) {
      log.error("Exception creating temporary file", e);
      throw e;
    }
    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempFile));) {
      bufferedWriter.write(xmlContent);
    } catch (IOException e) {
      log.error("Exception writing in temporary file", e);
      throw e;
    }
    return tempFile.getAbsolutePath();
  }
  
  public static String readFile(String completePathAndFilename) throws IOException {
    Path path = FileSystems.getDefault().getPath(completePathAndFilename);
    return new String(Files.readAllBytes(path));
  }
  
}
