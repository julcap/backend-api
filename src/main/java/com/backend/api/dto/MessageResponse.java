package com.backend.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageResponse {
    private final String message;

    public MessageResponse(String message) {
        this.message = message;
    }
}
