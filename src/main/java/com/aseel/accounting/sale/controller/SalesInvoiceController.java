package com.aseel.accounting.sale.controller;

import com.aseel.accounting.sale.dto.SalesInvoiceRequestDTO;
import com.aseel.accounting.sale.dto.SalesInvoiceResponseDTO;
import com.aseel.accounting.sale.service.SalesInvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SalesInvoiceController {

    private final SalesInvoiceService salesInvoiceService;

    public SalesInvoiceController(SalesInvoiceService salesInvoiceService) {
        this.salesInvoiceService = salesInvoiceService;
    }

    @GetMapping("/company/{companyBusinessCode}")
    public ResponseEntity<List<SalesInvoiceResponseDTO>> getInvoicesByCompany(@PathVariable String companyBusinessCode) {
        List<SalesInvoiceResponseDTO> invoices = salesInvoiceService.getInvoicesByCompany(companyBusinessCode);
        return ResponseEntity.ok(invoices);
    }

    @PostMapping
    public ResponseEntity<SalesInvoiceResponseDTO> createInvoice(@RequestBody SalesInvoiceRequestDTO requestDTO) {
        SalesInvoiceResponseDTO savedInvoice = salesInvoiceService.saveInvoice(requestDTO);
        return ResponseEntity.ok(savedInvoice);
    }
}
