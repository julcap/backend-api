package com.backend.api.models;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String phone;
    private LocalDate birthdate;
    private String country;
    private String address;

    @Builder
    public User(String name, String surname, String phone, LocalDate birthdate, String country, String address) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.birthdate = birthdate;
        this.country = country;
        this.address = address;
    }
}
