package com.example.lab1.presentation.exception;

import java.net.URI;

public record ErrorResponse (
    URI type,
    String title,
    int status,
    String detail,
    URI instance
) {}
