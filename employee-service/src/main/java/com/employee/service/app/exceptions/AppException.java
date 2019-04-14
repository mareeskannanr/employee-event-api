package com.employee.service.app.exceptions;

public class AppException extends RuntimeException {

    private String message;

    public AppException(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return message;
    }

}
