package com.medfactor.factorusers.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
public class FileController {



    @Value("${file.upload-dir}")
    private String uploadDir;



    @PostMapping("/upload")
    public ResponseEntity<Map<String,String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Content-Type") String contentType) {

        System.out.println("Received file: " + file.getOriginalFilename());

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        if (!contentType.startsWith("multipart/form-data")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid content type"));
        }

        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path filePath = uploadPath.resolve(fileName);

            Files.createDirectories(uploadPath);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok(Map.of(
                    "path", filePath.toString(),
                    "fileName", fileName,
                    "originalName", file.getOriginalFilename()
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to upload file: " + e.getMessage()));
        }
    }
}
