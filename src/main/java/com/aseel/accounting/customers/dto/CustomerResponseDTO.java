package com.aseel.accounting.customers.dto;


import com.aseel.accounting.customers.model.Customer;

public class CustomerResponseDTO {
    private Long id;
    private String companyBusinessCode;
    private String customerCode;
    private String name;
    private String phone;
    private String address;
    private String notes;

    public CustomerResponseDTO(Customer customer) {
        this.id = customer.getId();
        this.companyBusinessCode = customer.getCompanyBusinessCode();
        this.customerCode = customer.getCustomerCode();
        this.name = customer.getName();
        this.phone = customer.getPhone();
        this.address = customer.getAddress();
        this.notes = customer.getNotes();
    }

    // Getters
    public Long getId() { return id; }
    public String getCompanyBusinessCode() { return companyBusinessCode; }
    public String getCustomerCode() { return customerCode; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getNotes() { return notes; }
}
