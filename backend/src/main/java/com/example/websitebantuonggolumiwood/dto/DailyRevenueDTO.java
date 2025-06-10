package com.example.websitebantuonggolumiwood.dto;

import java.time.LocalDate;

public class DailyRevenueDTO {
    private LocalDate date;
    private Double revenue;  // đổi từ Float sang Double

    public DailyRevenueDTO(LocalDate date, Double revenue) {
        this.date = date;
        this.revenue = revenue;
    }

    // getter/setter...

    public LocalDate getDate() {
        return date;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

    public DailyRevenueDTO() {
    }
}
