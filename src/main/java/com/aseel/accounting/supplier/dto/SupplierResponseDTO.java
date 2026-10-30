package com.aseel.accounting.supplier.dto;

import com.aseel.accounting.supplier.model.Supplier;

public class SupplierResponseDTO {
    private Long id;
    private String companyBusinessCode;
    private String supplierCode;
    private String name;
    private String phone;
    private String address;
    private String notes;

    public SupplierResponseDTO(Supplier supplier) {
        this.id = supplier.getId();
        this.companyBusinessCode = supplier.getCompanyBusinessCode();
        this.supplierCode = supplier.getSupplierCode();
        this.name = supplier.getName();
        this.phone = supplier.getPhone();
        this.address = supplier.getAddress();
        this.notes = supplier.getNotes();
    }

    // Getters
    public Long getId() { return id; }
    public String getCompanyBusinessCode() { return companyBusinessCode; }
    public String getSupplierCode() { return supplierCode; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getNotes() { return notes; }
}
