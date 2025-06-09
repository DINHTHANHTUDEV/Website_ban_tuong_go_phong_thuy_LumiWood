package com.example.websitebantuonggolumiwood.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ShippingMethods")
public class ShippingMethodOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true , name = "name")
    private String name;


    @Column(name = "description")
    private String description;

    @Column(name = "base_cost")
    private BigDecimal baseCost;

    @Column(name = "estimated_days_min")
    private Integer estimatedDaysMin;

    @Column(name = "estimated_days_max")
    private Integer estimatedDaysMax;

    @Column(name = "is_active")
    private Boolean isActive;
}
