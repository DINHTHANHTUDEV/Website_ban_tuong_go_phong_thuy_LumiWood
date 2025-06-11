package com.example.websitebantuonggolumiwood.controller;

import com.example.websitebantuonggolumiwood.dto.BasicStatsDTO;
import com.example.websitebantuonggolumiwood.dto.DailyRevenueDTO;
import com.example.websitebantuonggolumiwood.dto.TodayStatisticsDTO;
import com.example.websitebantuonggolumiwood.repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
public class DashboardControler {
    @Autowired
    private DashboardRepository ordersRepo;
    // GET /api/statistics/order-count-between?startDate=2025-05-05&endDate=2025-05-13
    @GetMapping("/order-count-between")
    public Long getOrderCountBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Date start = java.sql.Date.valueOf(startDate);
        Date end = java.sql.Date.valueOf(endDate);

        Long count = ordersRepo.getOrderCountBetweenDates(start, end);
        return count != null ? count : 0L;
    }


    // GET /api/statistics/revenue?month=5&year=2025
    @GetMapping("/revenue")
    public Float getRevenueByMonth(@RequestParam int month, @RequestParam int year) {
        Float revenue = ordersRepo.getRevenueByMonth(month, year);
        return revenue != null ? revenue : 0f;
    }

    // GET /api/statistics/orders?month=5&year=2025
    @GetMapping("/orders")
    public Long getOrderCountByMonth(@RequestParam int month, @RequestParam int year) {
        Long count = ordersRepo.getOrderCountByMonth(month, year);
        return count != null ? count : 0L;
    }

    // GET /api/statistics/daily-revenue?startDate=2025-05-01&endDate=2025-05-20
    @GetMapping("/daily-revenue")
    public List<DailyRevenueDTO> getDailyRevenueBetween(
            @RequestParam("start")
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam("end")
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        List<Object[]> results = ordersRepo.getDailyRevenueNative(startDate, endDate);

        return results.stream()
                .map(row -> {
                    java.sql.Date date = (java.sql.Date) row[0];
                    Double revenue = ((Number) row[1]).doubleValue();
                    return new DailyRevenueDTO(date.toLocalDate(), revenue);
                })
                .collect(Collectors.toList());
    }

    // GET /api/statistics/today
    @GetMapping("/today")
    public TodayStatisticsDTO getTodayStatistics() {
        LocalDate today = LocalDate.now();
        Date start = java.sql.Date.valueOf(today);
        Date end = java.sql.Date.valueOf(today);

        Long orderCount = ordersRepo.getOrderCountBetweenDates(start, end);
        Double revenue = ordersRepo.getRevenueBetweenDates(start, end);

        return new TodayStatisticsDTO(
                orderCount != null ? orderCount : 0L,
                revenue != null ? revenue : 0.0
        );
    }
    // GET /api/statistics/last-7-days
    @GetMapping("/last-7-days")
    public BasicStatsDTO getLast7DaysStats() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6); // 7 ngày gần nhất tính cả hôm nay

        Date start = java.sql.Date.valueOf(startDate);
        Date end = java.sql.Date.valueOf(endDate);

        Long orderCount = ordersRepo.countOrdersBetween(start, end);
        Double totalRevenue = ordersRepo.sumRevenueBetween(start, end);

        return new BasicStatsDTO(
                orderCount != null ? orderCount : 0L,
                totalRevenue != null ? totalRevenue : 0.0
        );
    }

    // GET /api/statistics/current-month
    @GetMapping("/current-month")
    public BasicStatsDTO getCurrentMonthStats() {
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1); // ngày đầu tháng
        LocalDate endDate = now; // hôm nay

        Date start = java.sql.Date.valueOf(startDate);
        Date end = java.sql.Date.valueOf(endDate);

        Long orderCount = ordersRepo.countOrdersBetween(start, end);
        Double totalRevenue = ordersRepo.sumRevenueBetween(start, end);

        return new BasicStatsDTO(
                orderCount != null ? orderCount : 0L,
                totalRevenue != null ? totalRevenue : 0.0
        );
    }

    // GET /api/statistics/basic-stats
    @GetMapping("/basic-stats")
    public BasicStatsDTO getBasicStats() {
        Long totalOrders = ordersRepo.getTotalOrderCount();
        Double totalRevenue = ordersRepo.getTotalRevenue();

        return new BasicStatsDTO(
                totalOrders != null ? totalOrders : 0L,
                totalRevenue != null ? totalRevenue : 0.0
        );
    }
    @GetMapping("/revenueovertime")
    public BasicStatsDTO getStatsByDateRange(
            @RequestParam("start")
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam("end")
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        Long totalOrders = ordersRepo.countOrdersBetween(startDate, endDate);
        Double totalRevenue = ordersRepo.sumRevenueBetween(startDate, endDate);

        if (totalOrders == null) totalOrders = 0L;
        if (totalRevenue == null) totalRevenue = 0.0;

        return new BasicStatsDTO(totalOrders, totalRevenue);
    }

}

