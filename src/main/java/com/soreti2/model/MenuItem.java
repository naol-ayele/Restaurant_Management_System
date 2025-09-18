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

    // menu item basic info
    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String currency = "ETB"; // default currency

    private String quality; // e.g., "High", "Medium"

    // ItemType instead of Category
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType itemType;

    // image path
    private String image;

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
