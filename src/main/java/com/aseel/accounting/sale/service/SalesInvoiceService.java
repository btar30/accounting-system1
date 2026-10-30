package com.aseel.accounting.sale.service;

import com.aseel.accounting.sale.dto.SalesInvoiceRequestDTO;
import com.aseel.accounting.sale.dto.SalesInvoiceResponseDTO;
import java.util.List;

public interface SalesInvoiceService {
    List<SalesInvoiceResponseDTO> getInvoicesByCompany(String companyBusinessCode);
    SalesInvoiceResponseDTO saveInvoice(SalesInvoiceRequestDTO requestDTO);
}
