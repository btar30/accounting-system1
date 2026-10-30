package com.aseel.accounting.purchase.dto;

import java.time.LocalDate;
import java.util.List;

public class PurchaseInvoiceRequestDTO {
    private String companyBusinessCode;
    private String supplierCode;
    private LocalDate invoiceDate;
    private String notes;
    private List<PurchaseDetailDTO> details;

    // Getters and Setters
    public String getCompanyBusinessCode() { return companyBusinessCode; }
    public void setCompanyBusinessCode(String companyBusinessCode) { this.companyBusinessCode = companyBusinessCode; }

    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }

    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<PurchaseDetailDTO> getDetails() { return details; }
    public void setDetails(List<PurchaseDetailDTO> details) { this.details = details; }

    // DTO داخلي لتفاصيل الأصناف في الطلب
    public static class PurchaseDetailDTO {
        private String itemCode;
        private Double quantity;
        private Double purchasePrice;

        public String getItemCode() { return itemCode; }
        public void setItemCode(String itemCode) { this.itemCode = itemCode; }

        public Double getQuantity() { return quantity; }
        public void setQuantity(Double quantity) { this.quantity = quantity; }

        public Double getPurchasePrice() { return purchasePrice; }
        public void setPurchasePrice(Double purchasePrice) { this.purchasePrice = purchasePrice; }
    }
}
