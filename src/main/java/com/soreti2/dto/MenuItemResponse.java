package com.soreti2.dto;

import com.soreti2.model.ItemType;
import lombok.Data;

@Data
public class MenuItemResponse {
    private Long id;
    private String name;
    private ItemType type;
    private double price;
    private int stock;
}
