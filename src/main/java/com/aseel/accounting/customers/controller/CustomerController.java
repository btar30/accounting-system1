package com.aseel.accounting.customers.controller;

import com.aseel.accounting.customers.dto.CustomerRequestDTO;
import com.aseel.accounting.customers.dto.CustomerResponseDTO;
import com.aseel.accounting.customers.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/company/{companyBusinessCode}")
    public ResponseEntity<List<CustomerResponseDTO>> getCustomersByCompany(@PathVariable String companyBusinessCode) {
        List<CustomerResponseDTO> customers = customerService.getCustomersByCompany(companyBusinessCode);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/company/{companyBusinessCode}/code/{customerCode}")
    public ResponseEntity<CustomerResponseDTO> getCustomerByCode(
            @PathVariable String companyBusinessCode,
            @PathVariable String customerCode) {
        CustomerResponseDTO customer = customerService.getCustomerByCode(companyBusinessCode, customerCode);
        return ResponseEntity.ok(customer);
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@RequestBody CustomerRequestDTO requestDTO) {
        CustomerResponseDTO savedCustomer = customerService.saveCustomer(requestDTO);
        return ResponseEntity.ok(savedCustomer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok().build();
    }
}
