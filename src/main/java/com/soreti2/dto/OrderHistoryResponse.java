package com.soreti2.dto;

import com.soreti2.model.ItemStatus;
import com.soreti2.model.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderHistoryResponse {
    private Long orderId;
    private int tableNumber;
    private String waiterName;
    private List<OrderItemSummary> items;
    private OrderStatus status;
    private LocalDateTime createdAt;

    @Data
    public static class OrderItemSummary {
        private String itemName;
        private int quantity;
        private ItemStatus status;
    }
}
