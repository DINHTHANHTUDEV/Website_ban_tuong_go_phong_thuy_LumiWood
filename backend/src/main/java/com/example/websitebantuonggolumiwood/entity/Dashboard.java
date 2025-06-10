package com.example.websitebantuonggolumiwood.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "orders")
public class Dashboard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "order_date")
    private Date orderDate;

    @Column(name = "total_amount")
    private Float totalAmount;

    @Column(name = "status")
    private String status;

    @Column(name = "deposit_amount")
    private Float depositAmount;

    @Column(name = "deposit_status")
    private String depositStatus;

    public Dashboard() {
    }

    public Dashboard(Date orderDate, Float totalAmount, String status, Float depositAmount, String depositStatus) {
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.depositAmount = depositAmount;
        this.depositStatus = depositStatus;
    }

    // Getter & Setter cho id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Float getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(Float depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getDepositStatus() {
        return depositStatus;
    }

    public void setDepositStatus(String depositStatus) {
        this.depositStatus = depositStatus;
    }
}
