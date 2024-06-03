package com.ajay.file_handling.controller;

import com.ajay.file_handling.service.FileService;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping(value = "/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/upload")
    public ResponseEntity<String> processFile(@RequestParam("file") MultipartFile file) throws CsvValidationException, IOException {
        String response = fileService.processFile(file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(value = "/download")
    public ResponseEntity<Resource> getFile() {
        Resource response = fileService.prepareFile();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=persons.xlsx")
                .body(response);
    }
}

