package com.employee.service.app.services;

import com.employee.service.app.exceptions.AppException;
import com.employee.service.app.exceptions.EmployeeNotFoundException;
import com.employee.service.app.models.Action;
import com.employee.service.app.models.Department;
import com.employee.service.app.models.Employee;
import com.employee.service.app.repositories.DepartmentRepository;
import com.employee.service.app.repositories.EmployeeRepository;
import com.employee.service.app.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


import static com.employee.service.app.utils.AppConstants.*;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final DepartmentRepository departmentRepository;

    private final EmployeeRepository employeeRepository;

    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public EmployeeServiceImpl(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository, KafkaProducerService kafkaProducerService) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    public Department saveDepartment(Department department) {

        //Check for Duplicate Department Name
        if(departmentRepository.findByName(department.getName()) != null) {
            throw new AppException(DEPARTMENT_EXISTS);
        }

        return departmentRepository.save(department);
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        Employee previousEmployee = null;
        Action action = Action.CREATE;

        //Check UUID in DB for update
        if(employee.getUuid() != null) {
            Optional<Employee> employeeOptional = employeeRepository.findById(employee.getUuid());
            if(!employeeOptional.isPresent()) {
                throw new EmployeeNotFoundException();
            }

            previousEmployee = employeeOptional.get();
            action = Action.UPDATE;
        }

        //Check for duplicate email Id for other employee's uuid
        previousEmployee = employeeRepository.findByEmail(employee.getEmail());
        if(previousEmployee != null && (employee.getUuid() == null || !previousEmployee.getUuid().equals(employee.getUuid()))) {
            throw new AppException(EMAIL_EXISTS);
        }

        //Check for Valid Department Id
        if(employee.getDepartmentId() != null) {
            Optional<Department> departmentOptional = departmentRepository.findById(employee.getDepartmentId());
            if(!departmentOptional.isPresent()) {
                throw new AppException(DEPARTMENT_NOT_EXISTS);
            }

            employee.setDepartment(departmentOptional.get());
        }

        employeeRepository.save(employee);

        String message = CommonUtils.generateMessage(employee.getUuid(), action);
        kafkaProducerService.sendMessage(message);

        return employee;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Employee getEmployeeById(UUID uuid) {
        Optional<Employee> employeeOptional = employeeRepository.findById(uuid);
        if(!employeeOptional.isPresent()) {
            throw new EmployeeNotFoundException();
        }

        return employeeOptional.get();
    }

    @Override
    public void deleteEmployee(UUID uuid) {
        Optional<Employee> employeeOptional = employeeRepository.findById(uuid);
        if(!employeeOptional.isPresent()) {
            throw new EmployeeNotFoundException();
        }

        employeeRepository.deleteById(uuid);
        String message = CommonUtils.generateMessage(uuid, Action.DELETE);
        kafkaProducerService.sendMessage(message);
    }

}