package com.example.lab1.presentation.exception;

import lombok.Data;

import java.net.URI;

@Data
public class ErrorResponse {
    private URI type;
    private String title;
    private int status;
    private String detail;
    private URI instance;

    public ErrorResponse(URI type, String title, int status, String detail, URI instance) {
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
    }
}
