package com.soreti2.controller;

import com.soreti2.dto.MenuItemResponse;
import com.soreti2.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuItemService menuItemService;

    @GetMapping("/qr/{tableId}")
    public ResponseEntity<List<MenuItemResponse>> getMenuForCustomer(@PathVariable String tableId) {
        // Optionally validate tableId
        return ResponseEntity.ok(menuItemService.getAllMenuItems());
    }
}
