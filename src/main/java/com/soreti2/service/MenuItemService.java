package com.soreti2.service;

import com.soreti2.dto.MenuItemRequest;
import com.soreti2.dto.MenuItemResponse;
import com.soreti2.model.MenuItem;
import com.soreti2.model.Role;
import com.soreti2.model.User;
import com.soreti2.repository.MenuItemRepository;
import com.soreti2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;
    private final WebSocketNotificationService wsNotificationService;


    public MenuItemResponse createMenuItem(MenuItemRequest request) {
        MenuItem item = MenuItem.builder()
                .name(request.getName())
                .type(request.getType())
                .price(request.getPrice())
                .stock(request.getStock())
                .build();
        MenuItem saved = menuItemRepository.save(item);
        return mapToResponse(saved);
    }

    public List<MenuItemResponse> getAllMenuItems() {
        return menuItemRepository.findAll()
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    public MenuItemResponse updateMenuItem(Long id, MenuItemRequest request) {
        MenuItem item = menuItemRepository.findById(id).orElseThrow();
        item.setName(request.getName());
        item.setType(request.getType());
        item.setPrice(request.getPrice());
        item.setStock(request.getStock());
        MenuItem saved = menuItemRepository.save(item);

        if (saved.getStock() <= 5) { // threshold
            List<User> managers = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.MANAGER).toList();
            for (User m : managers) {
                wsNotificationService.sendNotification(m.getId(),
                        "Low stock alert: " + saved.getName() + " (" + saved.getStock() + " left)");
            }
        }
        return mapToResponse(saved);
    }


    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }

    private MenuItemResponse mapToResponse(MenuItem item) {
        MenuItemResponse response = new MenuItemResponse();
        response.setId(item.getId());
        response.setName(item.getName());
        response.setType(item.getType());
        response.setPrice(item.getPrice());
        response.setStock(item.getStock());
        return response;
    }

    public List<MenuItemResponse> getLowStockItems(int threshold) {
        return menuItemRepository.findAll().stream()
                .filter(item -> item.getStock() <= threshold)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

}