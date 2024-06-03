package com.ajay.file_handling.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "person")
public class Person {
    @Id
    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private LocalDate dob;

    private Address address;
}
