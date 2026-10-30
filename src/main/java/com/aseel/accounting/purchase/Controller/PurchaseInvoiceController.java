package com.aseel.accounting.purchase.Controller;

import com.aseel.accounting.purchase.dto.PurchaseInvoiceRequestDTO;
import com.aseel.accounting.purchase.dto.PurchaseInvoiceResponseDTO;
import com.aseel.accounting.purchase.service.PurchaseInvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseInvoiceController {

    private final PurchaseInvoiceService purchaseInvoiceService;

    public PurchaseInvoiceController(PurchaseInvoiceService purchaseInvoiceService) {
        this.purchaseInvoiceService = purchaseInvoiceService;
    }

    @GetMapping("/company/{companyBusinessCode}")
    public ResponseEntity<List<PurchaseInvoiceResponseDTO>> getInvoicesByCompany(@PathVariable String companyBusinessCode) {
        List<PurchaseInvoiceResponseDTO> invoices = purchaseInvoiceService.getInvoicesByCompany(companyBusinessCode);
        return ResponseEntity.ok(invoices);
    }

    @PostMapping
    public ResponseEntity<PurchaseInvoiceResponseDTO> createInvoice(@RequestBody PurchaseInvoiceRequestDTO requestDTO) {
        PurchaseInvoiceResponseDTO savedInvoice = purchaseInvoiceService.saveInvoice(requestDTO);
        return ResponseEntity.ok(savedInvoice);
    }
}
