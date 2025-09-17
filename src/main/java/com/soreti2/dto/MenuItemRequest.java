package com.soreti2.dto;

import com.soreti2.model.ItemType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MenuItemRequest {
    @NotBlank
    private String name;

    private ItemType type;

    @Min(0)
    private double price;

    @Min(0)
    private int stock;
}