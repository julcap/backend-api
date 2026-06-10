package com.backend.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateUser() throws Exception {
        String json = """
                {
                  "name": "John",
                  "surname": "Doe",
                  "phone": "12345678",
                  "birthdate": "1990-05-20",
                  "country": "Denmark",
                  "address": "Some address"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"))
                .andExpect(jsonPath("$.phone").value("12345678"))
                .andExpect(jsonPath("$.birthdate").value("1990-05-20"))
                .andExpect(jsonPath("$.country").value("Denmark"))
                .andExpect(jsonPath("$.address").value("Some address"));
    }


    @Test
    void shouldReturnValidationErrors() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name")
                        .value("Name is required"))
                .andExpect(jsonPath("$.surname")
                        .value("Surname is required"))
                .andExpect(jsonPath("$.birthdate")
                        .value("Date of birth is required"));
    }

    @Test
    void shouldRejectInvalidBirthdateFormat() throws Exception {
        String json = """
            {
              "name": "John",
              "surname": "Doe",
              "birthdate": "ABC"
            }
            """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.birthdate")
                        .value("Invalid date format. Expected format: yyyy-MM-dd"));
    }

    @Test
    void shouldRejectFutureBirthdate() throws Exception {
        String json = """
            {
              "name": "John",
              "surname": "Doe",
              "birthdate": "%s"
            }
            """.formatted(
                LocalDate.now().plusDays(1)
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.birthdate")
                        .value("Date of birth must be a past date"));
    }

    @Test
    void shouldRejectBlankName() throws Exception {
        String json = """
            {
              "name": "",
              "surname": "Doe",
              "birthdate": "1990-05-20"
            }
            """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name")
                        .value("Name is required"));
    }

    @Test
    void shouldRejectBlankSurname() throws Exception {
        String json = """
            {
              "name": "John",
              "surname": "",
              "birthdate": "1990-05-20"
            }
            """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.surname")
                        .value("Surname is required"));
    }
}