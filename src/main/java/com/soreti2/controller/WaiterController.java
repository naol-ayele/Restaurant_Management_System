package com.soreti2.controller;

import com.soreti2.dto.MenuItemResponse;
import com.soreti2.model.Notification;
import com.soreti2.service.MenuItemService;
import com.soreti2.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/waiter")
@RequiredArgsConstructor
public class WaiterController {

    private final OrderService orderService;
    private final MenuItemService menuItemService;

    /**
     * ✅ Waiter menu arguuf
     * URL: GET /api/waiter/menu
     * Role: WAITER
     */
    @GetMapping("/menu")
    @PreAuthorize("hasRole('WAITER')")
    public ResponseEntity<List<MenuItemResponse>> getMenuForWaiter() {
        return ResponseEntity.ok(menuItemService.getAllMenuItems());
    }

    /**
     * ✅ Order serve godhuu
     * URL: POST /api/waiter/serve/{orderId}
     */
    @PostMapping("/serve/{orderId}")
    @PreAuthorize("hasRole('WAITER')")
    public ResponseEntity<String> serveOrder(@PathVariable Long orderId) {
        orderService.serveOrder(orderId);
        return ResponseEntity.ok("Order served");
    }

    /**
     * ✅ Notifications argachuu
     * URL: GET /api/waiter/notifications/{waiterId}
     */
    @GetMapping("/notifications/{waiterId}")
    @PreAuthorize("hasRole('WAITER')")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long waiterId) {
        return ResponseEntity.ok(orderService.getNotifications(waiterId));
    }

    /**
     * ✅ Notification akka dubbifame mark godhuu
     * URL: PUT /api/waiter/notifications/{notificationId}/read
     */
    @PutMapping("/notifications/{notificationId}/read")
    @PreAuthorize("hasRole('WAITER')")
    public ResponseEntity<String> markNotificationRead(@PathVariable Long notificationId) {
        orderService.markNotificationRead(notificationId);
        return ResponseEntity.ok("Notification marked as read");
    }
}
