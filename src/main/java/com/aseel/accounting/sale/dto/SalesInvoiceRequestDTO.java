package com.aseel.accounting.sale.dto;

import java.time.LocalDate;
import java.util.List;

public class SalesInvoiceRequestDTO {
    private String companyBusinessCode;
    private String customerCode;
    private LocalDate invoiceDate;
    private String notes;
    private List<SalesDetailDTO> details;

    // Getters and Setters
    public String getCompanyBusinessCode() { return companyBusinessCode; }
    public void setCompanyBusinessCode(String companyBusinessCode) { this.companyBusinessCode = companyBusinessCode; }

    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }

    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<SalesDetailDTO> getDetails() { return details; }
    public void setDetails(List<SalesDetailDTO> details) { this.details = details; }

    // DTO داخلي لتفاصيل الأصناف المباعة
    public static class SalesDetailDTO {
        private String itemCode;
        private Double quantity;
        private Double salePrice;

        public String getItemCode() { return itemCode; }
        public void setItemCode(String itemCode) { this.itemCode = itemCode; }

        public Double getQuantity() { return quantity; }
        public void setQuantity(Double quantity) { this.quantity = quantity; }

        public Double getSalePrice() { return salePrice; }
        public void setSalePrice(Double salePrice) { this.salePrice = salePrice; }
    }
}
