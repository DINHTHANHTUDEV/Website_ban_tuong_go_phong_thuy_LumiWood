package com.example.websitebantuonggolumiwood.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.websitebantuonggolumiwood.entity.ProductOrderHistory;
import com.example.websitebantuonggolumiwood.entity.OrdersHistory;

import java.math.BigDecimal;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

//    @ManyToOne
//    @JoinColumn(name = "product_id", insertable = false, updatable = false)
//    private  ProductOrderHistory product;

    @Column(name = "product_id")
    private Integer productId;


    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price_at_purchase")
    private BigDecimal priceAtPurchase;

//    @ManyToOne
//    @JoinColumn(name = "order_id")
//    private OrdersHistory order;

    @ManyToOne
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    // Getters/setters
}

