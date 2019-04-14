package com.employee.service.app.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.UUID;

import static com.employee.service.app.utils.AppConstants.*;

@Data
@Entity
@Table(name = EMPLOYEES)
@ApiModel(description = EMPLOYEE_MODEL_DESCRIPTION)
public class Employee {

    @Id
    @GeneratedValue
    @ApiModelProperty(notes = EMPLOYEE_UUID_NOTE)
    private UUID uuid;

    @Email(message = EMAIL_INVALID)
    @NotBlank(message = EMAIL_REQUIRED)
    @ApiModelProperty(notes = EMPLOYEE_EMAIL_NOTE, required = true, example = EMPLOYEE_EMAIL_EXAMPLE)
    private String email;

    @Column(name = FULL_NAME)
    private String fullName;

    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @ApiModelProperty(notes = EMPLOYEE_BIRTHDAY_NOTE)
    private LocalDate birthday;

    @Transient
    @ApiModelProperty(notes = EMPLOYEE_DEPARTMENT_NOTE)
    private Long departmentId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = DEPARTMENT_ID)
    private Department department;

    public Long getDepartmentId() {

        if (departmentId != null) {
            return departmentId;
        }

        if (department != null) {
            return department.getId();
        }

        return null;
    }
}
