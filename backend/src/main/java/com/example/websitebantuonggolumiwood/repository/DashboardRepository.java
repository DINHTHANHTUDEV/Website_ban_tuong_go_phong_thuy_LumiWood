package com.example.websitebantuonggolumiwood.repository;


import com.example.websitebantuonggolumiwood.entity.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, Integer> {

    @Query("SELECT SUM(d.totalAmount) FROM Dashboard d WHERE MONTH(d.orderDate) = :month AND YEAR(d.orderDate) = :year")
    Float getRevenueByMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(d) FROM Dashboard d WHERE MONTH(d.orderDate) = :month AND YEAR(d.orderDate) = :year")
    Long getOrderCountByMonth(@Param("month") int month, @Param("year") int year);

    @Query(value = "SELECT CAST(o.order_date AS date) AS date, SUM(o.total_amount) AS revenue " +
            "FROM orders o " +
            "WHERE o.order_date >= :startDate AND o.order_date < DATEADD(day, 1, :endDate) " +
            "GROUP BY CAST(o.order_date AS date) " +
            "ORDER BY CAST(o.order_date AS date)", nativeQuery = true)
    List<Object[]> getDailyRevenueNative(@Param("startDate") Date startDate,
                                         @Param("endDate") Date endDate);

    @Query(value = "SELECT COUNT(*) FROM orders o WHERE o.order_date >= :startDate AND o.order_date < DATEADD(day, 1, :endDate)", nativeQuery = true)
    Long getOrderCountBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT SUM(o.total_amount) FROM orders o WHERE o.order_date >= :startDate AND o.order_date < DATEADD(day, 1, :endDate)", nativeQuery = true)
    Double getRevenueBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT COUNT(d) FROM Dashboard d")
    Long getTotalOrderCount();

    @Query("SELECT SUM(d.totalAmount) FROM Dashboard d")
    Double getTotalRevenue();

    @Query("SELECT COUNT(d) FROM Dashboard d WHERE d.orderDate BETWEEN :start AND :end AND d.status = 'COMPLETED'")
    Long countOrdersBetween(@Param("start") Date start, @Param("end") Date end);

    @Query("SELECT COALESCE(SUM(d.totalAmount), 0) FROM Dashboard d WHERE d.orderDate BETWEEN :start AND :end AND d.status = 'COMPLETED'")
    Double sumRevenueBetween(@Param("start") Date start, @Param("end") Date end);
}
