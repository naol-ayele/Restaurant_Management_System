package com.soreti2.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 1000)
    private String description;

    private Double price;

    private String currency;

    private String quality;

    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    private String image; // save file path

    // Payment info
    private String cbeName;
    private String cbeNumber;
    private String coopName;
    private String coopNumber;
    private String awashName;
    private String awashNumber;
    private String teleBirrName;
    private String teleBirrNumber;
}
