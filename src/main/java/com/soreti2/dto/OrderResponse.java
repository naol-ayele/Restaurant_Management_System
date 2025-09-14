package com.soreti2.dto;

import com.soreti2.model.ItemStatus;
import com.soreti2.model.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {

    private Long orderId;
    private int tableNumber;
    private String waiterName;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;

    @Data
    @Builder
    public static class OrderItemResponse {
        private Long id;
        private String menuItemName;
        private int quantity;
        private double price;
        private ItemStatus status;
    }
}
