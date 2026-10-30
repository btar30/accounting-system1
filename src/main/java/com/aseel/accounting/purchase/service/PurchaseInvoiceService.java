package com.aseel.accounting.purchase.service;

import com.aseel.accounting.purchase.dto.PurchaseInvoiceRequestDTO;
import com.aseel.accounting.purchase.dto.PurchaseInvoiceResponseDTO;
import java.util.List;

public interface PurchaseInvoiceService {
    List<PurchaseInvoiceResponseDTO> getInvoicesByCompany(String companyBusinessCode);
    PurchaseInvoiceResponseDTO saveInvoice(PurchaseInvoiceRequestDTO requestDTO);
}
