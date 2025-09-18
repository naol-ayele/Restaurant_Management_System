package com.soreti2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemRequest {
    private String name;
    private String description;
    private Double price;
    private String quality;
    private String currency;
    private String itemType; // String for frontend compatibility

    private String cbeName;
    private String cbeNumber;
    private String awashName;
    private String awashNumber;
    private String teleBirrName;
    private String teleBirrNumber;
    private String coopName;
    private String coopNumber;
}
