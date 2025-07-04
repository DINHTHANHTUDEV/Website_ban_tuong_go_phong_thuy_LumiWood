package com.example.websitebantuonggolumiwood.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class OrderAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "customer_name", length = 150)
    private String customerName;

    @Column(name = "customer_phone", length = 20)
    private String customerPhone;

    @Column(name = "customer_address", length = 500)
    private String customerAddress;

    @Column(name = "total_amount", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "promotion_id")
    private Integer promotionId;

    @Column(name = "discount_amount", precision = 18, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "guest_email", length = 255)
    private String guestEmail;

    @Column(name = "shipping_address_id")
    private Long shippingAddressId;

    @Column(name = "billing_address_id")
    private Long billingAddressId;

    @Column(name = "shipping_recipient_name", length = 150)
    private String shippingRecipientName;

    @Column(name = "shipping_recipient_phone", length = 20)
    private String shippingRecipientPhone;

    @Column(name = "shipping_street_address", length = 500)
    private String shippingStreetAddress;

    @Column(name = "shipping_ward", length = 100)
    private String shippingWard;

    @Column(name = "shipping_district", length = 100)
    private String shippingDistrict;

    @Column(name = "shipping_city", length = 100)
    private String shippingCity;

    @Column(name = "shipping_method_id") // ánh xạ ID
    private Integer shippingMethodId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_method_id", insertable = false, updatable = false) // ánh xạ đối tượng
    private ShippingMethod shippingMethod;

    @Column(name = "shipping_cost", precision = 18, scale = 2)
    private BigDecimal shippingCost;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "order_note", length = 500)
    private String orderNote;

    @Column(name = "cancel_reason", columnDefinition = "nvarchar(max)")
    private String cancelReason;

    @Column(name = "deposit_amount", precision = 18, scale = 2)
    private BigDecimal depositAmount;

    @Column(name = "deposit_status", length = 20)
    private String depositStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItemAdmin> orderItemAdmins;
}
