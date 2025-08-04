package com.ali.amara.market.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class AuctionStatistics {

    private String productCategory;
    private Long totalAuctions;
    private Double averageHighestBid;
    private Long totalBids; // Ou Double si vous avez SUM() sur un champ décimal

    // CE CONSTRUCTEUR EST LE PLUS IMPORTANT
    // Assurez-vous que les types et l'ordre correspondent à votre requête @Query
    public AuctionStatistics(String productCategory, Long totalAuctions, Double averageHighestBid, Long totalBids) {
        this.productCategory = productCategory;
        this.totalAuctions = totalAuctions;
        this.averageHighestBid = averageHighestBid;
        this.totalBids = totalBids;
    }

    // --- Getters et Setters ---

}