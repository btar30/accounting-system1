package com.aseel.accounting.supplier.service;

import com.aseel.accounting.supplier.dto.SupplierRequestDTO;
import com.aseel.accounting.supplier.dto.SupplierResponseDTO;

import java.util.List;

public interface SupplierService {

    List<SupplierResponseDTO> getSuppliersByCompany(String companyBusinessCode);

    SupplierResponseDTO getSupplierByCode(String companyBusinessCode, String supplierCode);

    SupplierResponseDTO saveSupplier(SupplierRequestDTO requestDTO);

    void deleteSupplier(Long id);
}
