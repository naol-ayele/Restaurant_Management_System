package com.soreti2.service;

import com.soreti2.dto.MenuItemRequest;
import com.soreti2.dto.MenuItemResponse;
import com.soreti2.model.ItemType;
import com.soreti2.model.MenuItem;
import com.soreti2.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final StorageService storageService;

    // Create Menu Item
    public MenuItemResponse createMenuItem(MenuItemRequest request, MultipartFile imageFile) {
        String fileName = storeImage(imageFile);

        MenuItem item = MenuItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .currency(request.getCurrency() != null ? request.getCurrency() : "ETB")
                .quality(request.getQuality())
                .itemType(parseItemType(request.getItemType()))
                .image(fileName)
                .cbeName(request.getCbeName())
                .cbeNumber(request.getCbeNumber())
                .coopName(request.getCoopName())
                .coopNumber(request.getCoopNumber())
                .awashName(request.getAwashName())
                .awashNumber(request.getAwashNumber())
                .teleBirrName(request.getTeleBirrName())
                .teleBirrNumber(request.getTeleBirrNumber())
                .build();

        return mapToResponse(menuItemRepository.save(item));
    }

    // Update Menu Item
    public MenuItemResponse updateMenuItem(Long id, MenuItemRequest request, MultipartFile imageFile) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        String fileName = storeImage(imageFile);

        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setCurrency(request.getCurrency() != null ? request.getCurrency() : "ETB");
        item.setQuality(request.getQuality());
        item.setItemType(parseItemType(request.getItemType()));

        if (fileName != null) {
            item.setImage(fileName);
        }

        item.setCbeName(request.getCbeName());
        item.setCbeNumber(request.getCbeNumber());
        item.setCoopName(request.getCoopName());
        item.setCoopNumber(request.getCoopNumber());
        item.setAwashName(request.getAwashName());
        item.setAwashNumber(request.getAwashNumber());
        item.setTeleBirrName(request.getTeleBirrName());
        item.setTeleBirrNumber(request.getTeleBirrNumber());

        return mapToResponse(menuItemRepository.save(item));
    }

    // Get all menu items
    public List<MenuItemResponse> getAllMenuItems() {
        return menuItemRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Delete menu item
    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new RuntimeException("Menu item not found");
        }
        menuItemRepository.deleteById(id);
    }

    // Map entity → response
    private MenuItemResponse mapToResponse(MenuItem item) {
        return MenuItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .currency(item.getCurrency())
                .quality(item.getQuality())
                .itemType(item.getItemType().name()) // String for frontend
                .image(item.getImage())
                .cbeName(item.getCbeName())
                .cbeNumber(item.getCbeNumber())
                .coopName(item.getCoopName())
                .coopNumber(item.getCoopNumber())
                .awashName(item.getAwashName())
                .awashNumber(item.getAwashNumber())
                .teleBirrName(item.getTeleBirrName())
                .teleBirrNumber(item.getTeleBirrNumber())
                .build();
    }

    // Store image
    private String storeImage(MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                return storageService.storeFile(imageFile);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store image", e);
            }
        }
        return null;
    }

    // Parse item type safely
    private ItemType parseItemType(String type) {
        try {
            return type != null ? ItemType.valueOf(type.toUpperCase()) : ItemType.BREAKFAST;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid item type: " + type);
        }
    }
}
