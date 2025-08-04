package com.ali.amara.market.dto;

// Assurez-vous d'importer l'enum OrderStatus si vous l'utilisez
import com.ali.amara.market.entity.Order.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class OrderStatistics {

    private OrderStatus status; // Le type doit correspondre à o.status
    private Long totalOrders;
    private BigDecimal totalRevenue; // Le type doit correspondre à SUM(o.totalAmount)

    // LE CONSTRUCTEUR CRUCIAL
    public OrderStatistics(OrderStatus status, Long totalOrders, BigDecimal totalRevenue) {
        this.status = status;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
    }
}