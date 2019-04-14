package com.employee.service.app.utils;


public class AppConstants {

    public static final String NAME_REQUIRED = "name is required";
    public static final String EMAIL_INVALID = "email is invalid";
    public static final String EMAIL_REQUIRED = "email is required";
    public static final String INVALID_POST_MSG = "object contains invalid data";
    public static final String INTERNAL_SERVER_ERROR_MSG = "sorry, something went wrong!";
    public static final String EMAIL_EXISTS = "email already exists";
    public static final String DEPARTMENT_EXISTS = "department name already exists";
    public static final String DEPARTMENT_NOT_EXISTS = "department name doesn't exists";
    public static final String EMPLOYEE_NOT_EXISTS = "employee doesn't exists";

    public static final String USER_NAME = "${app.security.user.name}";
    public static final String PASSWORD = "${app.security.user.password}";


    public static final String DEPARTMENT = "department";
    public static final String EMPLOYEE = "employee";

    public static final String API = "/api/";
    public static final String UUID = "uuid";
    public static final String USER = "USER";
    public static final String KAFKA_TOPIC = "${app.kafka.topic}";
    public static final String EMPLOYEE_ID = EMPLOYEE + "/{" + UUID + "}";

    public static final String FULL_NAME = "full_name";
    public static final String DEPARTMENT_ID = "department_id";
    public static final String EMPLOYEES = EMPLOYEE + "s";
    public static final String DEPARTMENTS = DEPARTMENT + "s";

    public static final String CTRL_PACKAGE = "com.employee.service.app.controllers";
    public static final String SWAGGER_ENDPOINT = API + ".*";
    public static final String TITLE = "Employee Service API";
    public static final String DESCRIPTION = "A Service Exposing Endpoints To Manage Employees Of A Company";
    public static final String VERSION = "1.0.0";
    public static final String EMPLOYEES_TAG = "Employees";
    public static final String CTRL_DESCRIPTION = "REST APIs for Employee CURD Operations";
    public static final String CREATE_DEPARTMENT = "Create Department Name";
    public static final String CREATE_EMPLOYEE = "Create Employee";
    public static final String UPDATE_EMPLOYEE = "Update Employee Details";
    public static final String READ_EMPLOYEE = "Read Employee Information Using UUID";
    public static final String DELETE_EMPLOYEE = "Delete Employee Using UUID";
    public static final String BDAY_FORMAT = "\"birthday\" format <b>YYYY-MM-DD</b>";
    public static final String CREATE_DEP_NOTE = "\"id\" will generate automatically while saving department, So remove <b>\"id\"</b> property in your request body";
    public static final String CREATE_EMP_NOTE = "\"uuid\" will generate automatically while saving department, So remove <b>\"uuid\"</b> property in your request body \n" + BDAY_FORMAT;

    public static final String EMPLOYEE_MODEL_DESCRIPTION = "This model represents employees of various departments of a company";
    public static final String EMPLOYEE_EMAIL_NOTE = "Email Id must be unique i.e. 2 employees cannot have the same email.";
    public static final String EMPLOYEE_EMAIL_EXAMPLE = "employee@company.com";
    public static final String EMPLOYEE_UUID_NOTE = "Auto generated value when creating an employee. Don't include while creating new employee recor. But including while update employee record";
    public static final String EMPLOYEE_BIRTHDAY_NOTE = "Birthday should in the format (YYYY-MM-DD)";
    public static final String EMPLOYEE_DEPARTMENT_NOTE = "Existing Department's Id";

    public static final String DEPARTMENT_MODEL_DESCRIPTION = "This model represents various departments of a company";
    public static final String DEPARTMENT_NAME_NOTE = "Name must be unique i.e. 2 departments cannot have the same name.";
    public static final String DEPARTMENT_ID_NOTE = "Auto generated value when creating a department. Don't include while creating new department record";

}