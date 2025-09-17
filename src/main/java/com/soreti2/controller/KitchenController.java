package com.soreti2.controller;

import com.soreti2.model.ItemStatus;
import com.soreti2.model.Notification;
import com.soreti2.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kitchen")
@RequiredArgsConstructor
public class KitchenController {

    private final OrderService orderService;

    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getNotifications(userId));
    }

    @PutMapping("/item/{itemId}/status")
    public ResponseEntity<String> updateItemStatus(@PathVariable Long itemId,
                                                   @RequestParam ItemStatus status,
                                                   @RequestParam Long userId) {
        orderService.updateItemStatus(itemId, status, userId);
        return ResponseEntity.ok("Item status updated");
    }
}