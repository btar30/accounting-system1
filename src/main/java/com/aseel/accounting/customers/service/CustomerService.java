package com.aseel.accounting.customers.service;

import com.aseel.accounting.customers.dto.CustomerRequestDTO;
import com.aseel.accounting.customers.dto.CustomerResponseDTO;

import java.util.List;

public interface CustomerService {
    List<CustomerResponseDTO> getCustomersByCompany(String companyBusinessCode);
    CustomerResponseDTO getCustomerByCode(String companyBusinessCode, String customerCode);
    CustomerResponseDTO saveCustomer(CustomerRequestDTO requestDTO);
    void deleteCustomer(Long id);
}
