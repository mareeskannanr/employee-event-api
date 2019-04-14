package com.employee.service.app.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

import static com.employee.service.app.utils.AppConstants.*;


@Data
@Entity
@Table(name = DEPARTMENTS)
@ApiModel(description = DEPARTMENT_MODEL_DESCRIPTION)
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = DEPARTMENT_ID_NOTE)
    private Long id;

    @NotBlank(message = NAME_REQUIRED)
    @ApiModelProperty(notes = DEPARTMENT_NAME_NOTE, required = true)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = DEPARTMENT)
    private Set<Employee> employees;

}