package com.soreti2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soreti2.dto.MenuItemRequest;
import com.soreti2.dto.MenuItemResponse;
import com.soreti2.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final MenuItemService menuItemService;
    private final ObjectMapper objectMapper; // Jackson ObjectMapper for parsing JSON

    // ✅ Create menu item
    @PostMapping(value = "/menu", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MenuItemResponse> createMenuItem(
            @RequestPart("menu") String menuJson, // receive JSON as string
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws Exception {
        // Parse JSON manually
        MenuItemRequest request = objectMapper.readValue(menuJson, MenuItemRequest.class);
        MenuItemResponse response = menuItemService.createMenuItem(request, file);
        return ResponseEntity.ok(response);
    }

    // ✅ Update menu item
    @PutMapping(value = "/menu/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable Long id,
            @RequestPart("menu") String menuJson,
            @RequestPart(value = "image", required = false) MultipartFile file
    ) throws Exception {
        MenuItemRequest request = objectMapper.readValue(menuJson, MenuItemRequest.class);
        MenuItemResponse response = menuItemService.updateMenuItem(id, request, file);
        return ResponseEntity.ok(response);
    }

    // ✅ Get all menu items
    @GetMapping("/menu")
    public ResponseEntity<List<MenuItemResponse>> getAllMenuItems() {
        return ResponseEntity.ok(menuItemService.getAllMenuItems());
    }

    // ✅ Delete menu item
    @DeleteMapping("/menu/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}
