package com.ali.amara.market.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PriceTrend {

    private String productName;
    private String variety;
    private BigDecimal averagePrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    // CONSTRUCTEUR qui doit correspondre à votre requête
    public PriceTrend(String productName, String variety, BigDecimal averagePrice, BigDecimal minPrice, BigDecimal maxPrice) {
        this.productName = productName;
        this.variety = variety;
        this.averagePrice = averagePrice;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

}