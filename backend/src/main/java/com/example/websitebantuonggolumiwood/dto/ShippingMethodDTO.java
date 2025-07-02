package com.example.websitebantuonggolumiwood.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ShippingMethodDTO {

    private Integer id;

    private String name;

    private String description;

    private BigDecimal baseCost;

    private Integer estimatedDaysMin;

    private Integer estimatedDaysMax;


    private Boolean isActive;

}
