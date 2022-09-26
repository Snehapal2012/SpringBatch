package com.example.spring.batch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "company")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {
    @Id
    @Column(name = "serial_number")
    private long id;
    @Column(name = "company_name")
    private String companyName;
    @Column(name = "employee_name")
    private String employeeName;
    @Column(name = "description")
    private String description;
    @Column(name = "leave")
    private long leave;


}
