package com.employee.service.app.models;


import com.employee.service.app.utils.AppConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;


@Data
@Entity
@Table(name = AppConstants.DEPARTMENTS)
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = AppConstants.NAME_REQUIRED)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "department")
    private Set<Employee> employees;

}