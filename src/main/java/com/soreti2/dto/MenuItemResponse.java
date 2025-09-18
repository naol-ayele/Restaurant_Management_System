package com.soreti2.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String currency;
    private String quality;
    private String itemType;
    private String image;

    private String cbeName;
    private String cbeNumber;
    private String coopName;
    private String coopNumber;
    private String awashName;
    private String awashNumber;
    private String teleBirrName;
    private String teleBirrNumber;
}
