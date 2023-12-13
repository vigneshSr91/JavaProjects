package com.bank.account.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter @Setter @ToString
public class Customer {
    @Column(name="customer_id")
    @Id
    private int customerId;
    private String name;
    @Column(name="mobile_number")
    private String mobileNumber;

    private String email;
    @Column(name="create_dt")
    private LocalDate createDt;
}
