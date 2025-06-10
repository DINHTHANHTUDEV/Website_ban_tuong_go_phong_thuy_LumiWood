package com.example.websitebantuonggolumiwood.dto;

public class BasicStatsDTO {
    private Long totalOrders;
    private Double totalRevenue;

    public BasicStatsDTO() {}

    public BasicStatsDTO(Long totalOrders, Double totalRevenue) {
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}

