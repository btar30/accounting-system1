package com.aseel.accounting.item.controller;

import com.aseel.accounting.item.dto.ItemRequestDTO;
import com.aseel.accounting.item.dto.ItemResponseDTO;
import com.aseel.accounting.item.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // جلب جميع أصناف شركة معينة
    @GetMapping("/company/{companyBusinessCode}")
    public ResponseEntity<List<ItemResponseDTO>> getItemsByCompany(@PathVariable String companyBusinessCode) {
        List<ItemResponseDTO> items = itemService.getItemsByCompany(companyBusinessCode);
        return ResponseEntity.ok(items);
    }

    // جلب صنف محدد بواسطة كود الشركة وكود الصنف
    @GetMapping("/company/{companyBusinessCode}/code/{itemCode}")
    public ResponseEntity<ItemResponseDTO> getItemByCode(
            @PathVariable String companyBusinessCode,
            @PathVariable String itemCode) {
        ItemResponseDTO item = itemService.getItemByCode(companyBusinessCode, itemCode);
        return ResponseEntity.ok(item);
    }

    // إضافة صنف جديد
    @PostMapping
    public ResponseEntity<ItemResponseDTO> createItem(@RequestBody ItemRequestDTO requestDTO) {
        ItemResponseDTO savedItem = itemService.saveItem(requestDTO);
        return ResponseEntity.ok(savedItem);
    }

    // حذف صنف
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok().build();
    }
}
