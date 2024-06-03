package com.ajay.file_handling.service.impl;

import com.ajay.file_handling.models.Address;
import com.ajay.file_handling.models.Person;
import com.ajay.file_handling.repository.PersonRepository;
import com.ajay.file_handling.service.FileService;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final PersonRepository personRepository;

    @Override
    public String processFile(MultipartFile file) throws CsvValidationException, IOException {
        String contentType = file.getContentType();
        if ("text/csv".equals(contentType)) {
            return handleCSVFile(file);
        } else if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)) {
            return handleXLSXFile(file);
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + contentType);
        }
    }

    @Override
    @Transactional
    public Resource prepareFile() {
        List<Person> personList = personRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("First Name");
        headerRow.createCell(1).setCellValue("Last Name");
        headerRow.createCell(2).setCellValue("Email");
        headerRow.createCell(3).setCellValue("Phone");
        headerRow.createCell(4).setCellValue("DOB");
        headerRow.createCell(5).setCellValue("Address Lane 1");
        headerRow.createCell(6).setCellValue("Address Lane 2");
        headerRow.createCell(7).setCellValue("City");
        headerRow.createCell(8).setCellValue("State");
        headerRow.createCell(9).setCellValue("Zipcode");

        int rowNum = 1;
        for (Person person : personList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(person.getFirstName());
            row.createCell(1).setCellValue(person.getLastName());
            row.createCell(2).setCellValue(person.getEmail());
            row.createCell(3).setCellValue(person.getPhone());
            row.createCell(4).setCellValue(person.getDob());
            row.createCell(5).setCellValue(person.getAddress().getAddressLane1());
            row.createCell(6).setCellValue(person.getAddress().getAddressLane2());
            row.createCell(7).setCellValue(person.getAddress().getCity());
            row.createCell(8).setCellValue(person.getAddress().getState());
            row.createCell(9).setCellValue(person.getAddress().getZipcode());
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
            return new ByteArrayResource(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String handleCSVFile(MultipartFile file) throws IOException, CsvValidationException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVReader csvReader = new CSVReader(reader)) {
            List<Person> personList = new ArrayList<>();
            String[] nextRecord;
            csvReader.readNext();
            while ((nextRecord = csvReader.readNext()) != null) {
                Person person = mapToPerson(nextRecord);
                personList.add(person);
            }
            personRepository.saveAll(personList);
            return "CSV file processed successfully";
        }
    }

    private String handleXLSXFile(MultipartFile file) throws IOException, InvalidFormatException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            List<Person> personList = new ArrayList<>();
            while (rowIterator.hasNext()) {
                Row currentRow = rowIterator.next();
                Person person = mapToPerson(currentRow);
                personList.add(person);
            }

            personRepository.saveAll(personList);
            return "Excel file processed successfully";
        }
    }

    private Person mapToPerson(String[] values) {
        Person person = new Person();
        person.setFirstName(values[0]);
        person.setLastName(values[1]);
        person.setEmail(values[2]);
        person.setPhone(values[3]);
        person.setDob(LocalDate.parse(values[4]));
        Address address = new Address();
        address.setAddressLane1(values[5]);
        address.setAddressLane2(values[6]);
        address.setCity(values[7]);
        address.setState(values[8]);
        address.setZipcode(values[9]);
        person.setAddress(address);
        return person;
    }

    private Person mapToPerson(Row row) {
        DataFormatter formatter = new DataFormatter();
        Person person = new Person();

        person.setFirstName(formatter.formatCellValue(row.getCell(0)));
        person.setLastName(formatter.formatCellValue(row.getCell(1)));
        person.setEmail(formatter.formatCellValue(row.getCell(2)));
        person.setPhone(formatter.formatCellValue(row.getCell(3)));

        // For date, you need to check if it's actually a date cell
        Cell dateCell = row.getCell(4);
        if (dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
            person.setDob(dateCell.getLocalDateTimeCellValue().toLocalDate());
        }
        Address address = new Address();
        address.setAddressLane1(formatter.formatCellValue(row.getCell(5)));
        address.setAddressLane2(formatter.formatCellValue(row.getCell(6)));
        address.setCity(formatter.formatCellValue(row.getCell(7)));
        address.setState(formatter.formatCellValue(row.getCell(8)));
        address.setZipcode(formatter.formatCellValue(row.getCell(9)));

        person.setAddress(address);
        return person;
    }
}