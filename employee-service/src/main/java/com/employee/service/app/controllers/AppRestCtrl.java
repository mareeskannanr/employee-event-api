package com.employee.service.app.controllers;

import com.employee.service.app.models.Department;
import com.employee.service.app.models.Employee;
import com.employee.service.app.services.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

import static com.employee.service.app.utils.AppConstants.*;

@RestController
@RequestMapping(API)
@Api(value = EMPLOYEES_TAG, description = CTRL_DESCRIPTION, tags = {EMPLOYEES_TAG})
public class AppRestCtrl {

    private EmployeeService employeeService;

    @Autowired
    public AppRestCtrl(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping(DEPARTMENT)
    @ApiOperation(value = CREATE_DEPARTMENT, response = Department.class, notes = CREATE_DEP_NOTE)
    public ResponseEntity saveDepartment(@Valid @RequestBody Department department) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.saveDepartment(department));
    }

    @PostMapping(EMPLOYEE)
    @ApiOperation(value = CREATE_EMPLOYEE, response = Employee.class, notes = CREATE_EMP_NOTE)
    public ResponseEntity createEmployee(@Valid @RequestBody Employee employee) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.saveEmployee(employee));
    }

    @PutMapping(EMPLOYEE)
    @ApiOperation(value = UPDATE_EMPLOYEE, response = Employee.class, notes = BDAY_FORMAT)
    public ResponseEntity updateEmployee(@Valid @RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.saveEmployee(employee));
    }

    @GetMapping(EMPLOYEE_ID)
    @ApiOperation(value = READ_EMPLOYEE, response = Employee.class)
    public ResponseEntity getEmployeeById(@PathVariable(UUID) UUID uuid) {
        return ResponseEntity.ok(employeeService.getEmployeeById(uuid));
    }

    @DeleteMapping(EMPLOYEE_ID)
    @ApiOperation(value = DELETE_EMPLOYEE, response = String.class)
    public ResponseEntity deleteEmployeeById(@PathVariable(UUID) UUID uuid) {
        employeeService.deleteEmployee(uuid);
        return ResponseEntity.noContent().build();
    }

}
