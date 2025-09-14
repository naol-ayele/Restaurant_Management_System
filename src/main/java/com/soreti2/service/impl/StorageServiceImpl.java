package com.soreti2.service.impl;


import com.soreti2.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    private final Path storageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public StorageServiceImpl() {
        try {
            Files.createDirectories(storageLocation); // create folder if not exist
        } catch (IOException e) {
            log.error("Could not create upload folder", e);
            throw new RuntimeException("Could not create upload folder", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Cannot store empty file");
        }

        // Clean file name
        String fileName = System.currentTimeMillis() + "_" + Paths.get(file.getOriginalFilename()).getFileName();
        Path targetLocation = storageLocation.resolve(fileName);

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return fileName; // store this path or filename in DB
    }

    @Override
    public byte[] loadFileAsBytes(String fileName) throws IOException {
        Path filePath = storageLocation.resolve(fileName).normalize();
        if (!Files.exists(filePath)) {
            throw new RuntimeException("File not found: " + fileName);
        }
        return Files.readAllBytes(filePath);
    }
}
