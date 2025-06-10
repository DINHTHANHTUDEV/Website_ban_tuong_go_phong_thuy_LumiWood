package com.example.websitebantuonggolumiwood.dto;

public class TodayStatisticsDTO {
    private Long orderCount;
    private Double totalRevenue;

    public TodayStatisticsDTO(Long orderCount, Double totalRevenue) {
        this.orderCount = orderCount;
        this.totalRevenue = totalRevenue;
    }

    public TodayStatisticsDTO() {
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
