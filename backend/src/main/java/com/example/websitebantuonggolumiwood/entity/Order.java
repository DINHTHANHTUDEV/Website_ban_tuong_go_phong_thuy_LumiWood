package com.example.websitebantuonggolumiwood.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "customer_address")
    private String customerAddress;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "status")
    private String status;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "payment_method")
    private String paymentMethod;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @Column(name = "shipping_recipient_name")
    private String shippingRecipientName;

    @Column(name = "shipping_recipient_phone")
    private String shippingRecipientPhone;

    @Column(name = "shipping_street_address")
    private String shippingStreetAddress;

    @Column(name = "shipping_ward")
    private String shippingWard;

    @Column(name = "shipping_district")
    private String shippingDistrict;

    @Column(name = "shipping_city")
    private String shippingCity;

    @Column(name = "shipping_cost")
    private BigDecimal shippingCost;

    @Column(name = "order_note")
    private String orderNote;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "deposit_amount")
    private BigDecimal depositAmount;

    @Column(name = "deposit_status")
    private String depositStatus;

    @Column(name = "guest_email")
    private String guestEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_method_id", insertable = false, updatable = false)
    private ShippingMethod shippingMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", insertable = false, updatable = false)
    private Promotion promotion;

    @Column(name = "shipping_method_id")
    private Integer shippingMethodId;


    @Column(name = "promotion_id")
    private Integer promotionId;
}