package com.employee.service.app.exceptions;

public class EmployeeNotFoundException extends AppException {

    public EmployeeNotFoundException() {
        super("Employee does not exists!");
    }

}
