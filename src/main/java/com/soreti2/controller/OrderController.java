package com.soreti2.controller;

import com.soreti2.dto.OrderHistoryResponse;
import com.soreti2.dto.OrderResponse;
import com.soreti2.dto.OrderUpdateRequest;
import com.soreti2.model.ItemStatus;
import com.soreti2.model.Order;
import com.soreti2.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestParam int tableNumber,
                                             @RequestParam Long waiterId,
                                             @RequestBody Map<Long, Integer> items) {
        Order order = orderService.createOrder(tableNumber, waiterId, items);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/item/{id}/status")
    public ResponseEntity<String> updateItemStatus(@PathVariable Long id,
                                                   @RequestParam ItemStatus status,
                                                   @RequestParam Long userId) {
        orderService.updateItemStatus(id, status, userId);
        return ResponseEntity.ok("Item status updated");
    }
    @PutMapping("/{orderId}/update")
    @PreAuthorize("hasRole('WAITER')")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable Long orderId,
            @RequestBody OrderUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateOrder(orderId, request));
    }

    // Waiter: remove order
    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('WAITER')")
    public ResponseEntity<Void> removeOrder(@PathVariable Long orderId) {
        orderService.removeOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    // Cashier & Manager: see all orders
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('CASHIER','MANAGER')")
    public ResponseEntity<List<OrderHistoryResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
