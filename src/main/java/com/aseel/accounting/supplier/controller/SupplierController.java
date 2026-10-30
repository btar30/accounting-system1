package com.aseel.accounting.supplier.controller;

import com.aseel.accounting.supplier.dto.SupplierRequestDTO;
import com.aseel.accounting.supplier.dto.SupplierResponseDTO;
import com.aseel.accounting.supplier.service.SupplierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping("/company/{companyBusinessCode}")
    public ResponseEntity<List<SupplierResponseDTO>> getSuppliersByCompany(@PathVariable String companyBusinessCode) {
        List<SupplierResponseDTO> suppliers = supplierService.getSuppliersByCompany(companyBusinessCode);
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/company/{companyBusinessCode}/code/{supplierCode}")
    public ResponseEntity<SupplierResponseDTO> getSupplierByCode(
            @PathVariable String companyBusinessCode,
            @PathVariable String supplierCode) {
        SupplierResponseDTO supplier = supplierService.getSupplierByCode(companyBusinessCode, supplierCode);
        return ResponseEntity.ok(supplier);
    }

    @PostMapping
    public ResponseEntity<SupplierResponseDTO> createSupplier(@RequestBody SupplierRequestDTO requestDTO) {
        SupplierResponseDTO savedSupplier = supplierService.saveSupplier(requestDTO);
        return ResponseEntity.ok(savedSupplier);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok().build();
    }
}
