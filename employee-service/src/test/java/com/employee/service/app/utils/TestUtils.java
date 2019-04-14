package com.employee.service.app.utils;

import com.employee.service.app.models.Department;
import com.employee.service.app.models.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpHeaders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestUtils {

    public static Department createDepartment() throws Exception {
        Department department = new Department();
        department.setId(1L);
        department.setName("Department-1");
        return department;
    }

    public static List<Employee> createEmployees(int length) throws Exception {
        List<Employee> employees = new ArrayList<>();

        for(int i=1; i<=length; i++) {
            Employee employee = new Employee();
            employee.setFullName("Employee-" + i);
            employee.setUuid(UUID.randomUUID());
            employee.setEmail("employee" + i + "@gmail.com");
            //employee.setDepartmentId(1L);
            employee.setBirthday(LocalDate.now());
            employees.add(employee);
        }

        return employees;
    }

    public static String createEmployee(String type) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Employee employee = createEmployees(1).get(0);
        if(type.equals("Create")) {
            employee.setUuid(null);
        }

        return mapper.writeValueAsString(employee);
    }

}
