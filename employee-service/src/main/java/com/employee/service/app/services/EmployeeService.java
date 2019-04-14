package com.employee.service.app.services;

import com.employee.service.app.models.Department;
import com.employee.service.app.models.Employee;

import java.util.UUID;

public interface EmployeeService {

    Department saveDepartment(Department department);

    Employee saveEmployee(Employee employee);

    Employee getEmployeeById(UUID uuid);

    void deleteEmployee(UUID uuid);

}