package com.ajay.file_handling.service;

import com.opencsv.exceptions.CsvValidationException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String processFile(MultipartFile file) throws CsvValidationException, IOException;

    Resource prepareFile();
}
