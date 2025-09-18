package com.soreti2.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface StorageService {
    String storeFile(MultipartFile file) throws IOException;  // store file and return filename
    byte[] loadFileAsBytes(String fileName) throws IOException; // load file as byte[]
}
