package com.employee.service.app.models;


import com.employee.service.app.utils.AppConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(name = AppConstants.EMPLOYEES)
public class Employee {

    @Id
    @GeneratedValue
    private UUID uuid;

    @Email(message = AppConstants.EMAIL_INVALID)
    @NotBlank(message = AppConstants.EMAIL_REQUIRED)
    private String email;

    @Column(name = AppConstants.FULL_NAME)
    private String fullName;

    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate birthday;

    @Transient
    private Long departmentId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = AppConstants.DEPARTMENT_ID)
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
