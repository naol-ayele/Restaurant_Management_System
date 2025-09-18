package com.soreti2.service;

import com.soreti2.dto.OrderHistoryResponse;
import com.soreti2.dto.OrderResponse;
import com.soreti2.dto.OrderUpdateRequest;
import com.soreti2.exception.ResourceNotFoundException;
import com.soreti2.model.*;
import com.soreti2.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final WebSocketNotificationService wsNotificationService;

    // Waiter creates an order
    public Order createOrder(int tableNumber, Long waiterId, Map<Long, Integer> itemQuantities) {
        User waiter = userRepository.findById(waiterId)
                .orElseThrow(() -> new ResourceNotFoundException("Waiter not found"));

        List<OrderItem> orderItems = new ArrayList<>();
        Order order = Order.builder()
                .tableNumber(tableNumber)
                .waiter(waiter)
                .status(OrderStatus.PENDING)
                .build();
        order = orderRepository.save(order);

        for (Map.Entry<Long, Integer> entry : itemQuantities.entrySet()) {
            MenuItem menuItem = menuItemRepository.findById(entry.getKey())
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

            int quantity = entry.getValue();

            OrderItem orderItem = OrderItem.builder()
                    .menuItem(menuItem)
                    .quantity(quantity)
                    .price(menuItem.getPrice() * quantity)
                    .status(ItemStatus.PENDING)
                    .order(order)
                    .build();

            orderItemRepository.save(orderItem);
            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        orderRepository.save(order);

        // Notify Chef/Bartender
        notifyKitchen(orderItems, waiter);

        return order;
    }

    private void notifyKitchen(List<OrderItem> items, User waiter) {
        for (OrderItem item : items) {
            User toUser = item.getMenuItem().getItemType() == ItemType.FOOD ? findChef() : findBartender();
            Notification notification = Notification.builder()
                    .fromUser(waiter)
                    .toUser(toUser)
                    .message("New order for table " + item.getOrder().getTableNumber() +
                            ": " + item.getMenuItem().getName())
                    .build();
            notificationRepository.save(notification);
        }
    }

    private User findChef() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.CHEF)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No chef available"));
    }

    private User findBartender() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.BARTENDER)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No bartender available"));
    }

    public void updateItemStatus(Long itemId, ItemStatus status, Long userId) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));
        item.setStatus(status);
        orderItemRepository.save(item);
        updateOrderStatus(item.getOrder());

        if (status == ItemStatus.READY) {
            String msg = item.getMenuItem().getName() + " is ready for table " + item.getOrder().getTableNumber();
            wsNotificationService.sendNotification(item.getOrder().getWaiter().getId(), msg);
        }
    }

    private void updateOrderStatus(Order order) {
        boolean allReady = order.getItems().stream()
                .allMatch(i -> i.getStatus() == ItemStatus.READY);
        boolean anyInProgress = order.getItems().stream()
                .anyMatch(i -> i.getStatus() == ItemStatus.IN_PROGRESS);

        if (allReady) order.setStatus(OrderStatus.READY);
        else if (anyInProgress) order.setStatus(OrderStatus.IN_PROGRESS);
        else order.setStatus(OrderStatus.PENDING);

        orderRepository.save(order);
    }

    public void serveOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.READY) {
            throw new RuntimeException("Order not ready to serve");
        }

        order.setStatus(OrderStatus.SERVED);
        orderRepository.save(order);
    }

    public List<Notification> getNotifications(Long userId) {
        return notificationRepository.findAll().stream()
                .filter(n -> n.getToUser().getId().equals(userId) && !n.isRead())
                .collect(Collectors.toList());
    }

    public void markNotificationRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<OrderHistoryResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(order -> {
            OrderHistoryResponse response = new OrderHistoryResponse();
            response.setOrderId(order.getId());
            response.setTableNumber(order.getTableNumber());
            response.setWaiterName(order.getWaiter().getFullname());
            response.setStatus(order.getStatus());
            response.setCreatedAt(order.getCreatedAt());
            response.setItems(order.getItems().stream().map(i -> {
                OrderHistoryResponse.OrderItemSummary item = new OrderHistoryResponse.OrderItemSummary();
                item.setItemName(i.getMenuItem().getName());
                item.setQuantity(i.getQuantity());
                item.setStatus(i.getStatus());
                return item;
            }).toList());
            return response;
        }).toList();
    }

    public void removeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Cannot remove a paid order");
        }

        orderRepository.delete(order);
    }

    @Transactional
    public OrderResponse updateOrder(Long orderId, OrderUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (request.getTableNumber() != null) {
            order.setTableNumber(request.getTableNumber());
        }

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (Map.Entry<Long, Integer> entry : request.getItems().entrySet()) {
                Long menuItemId = entry.getKey();
                int quantity = entry.getValue();

                MenuItem menuItem = menuItemRepository.findById(menuItemId)
                        .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .menuItem(menuItem)
                        .quantity(quantity)
                        .price(menuItem.getPrice() * quantity)
                        .status(ItemStatus.PENDING)
                        .build();

                orderItemRepository.save(orderItem);
            }
        }

        return mapToOrderResponse(orderRepository.save(order));
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .tableNumber(order.getTableNumber())
                .waiterName(order.getWaiter().getFullname())
                .status(order.getStatus())
                .items(order.getItems().stream()
                        .map(i -> OrderResponse.OrderItemResponse.builder()
                                .id(i.getId())
                                .menuItemName(i.getMenuItem().getName())
                                .quantity(i.getQuantity())
                                .price(i.getMenuItem().getPrice() * i.getQuantity())
                                .status(i.getStatus())
                                .build())
                        .toList())
                .build();
    }
}
