package com.aseel.accounting.purchase.dto;

import com.aseel.accounting.purchase.model.PurchaseInvoice;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class PurchaseInvoiceResponseDTO {
    private Long id;
    private String companyBusinessCode;
    private String invoiceNumber;
    private String supplierCode;
    private LocalDate invoiceDate;
    private Double totalAmount;
    private String notes;
    private List<DetailResponseDTO> details;

    public PurchaseInvoiceResponseDTO(PurchaseInvoice invoice) {
        this.id = invoice.getId();
        this.companyBusinessCode = invoice.getCompanyBusinessCode();
        this.invoiceNumber = invoice.getInvoiceNumber();
        this.supplierCode = invoice.getSupplierCode();
        this.invoiceDate = invoice.getInvoiceDate();
        this.totalAmount = invoice.getTotalAmount();
        this.notes = invoice.getNotes();
        if (invoice.getDetails() != null) {
            this.details = invoice.getDetails().stream()
                    .map(d -> new DetailResponseDTO(d.getItemCode(), d.getQuantity(), d.getPurchasePrice(), d.getSubTotal()))
                    .collect(Collectors.toList());
        }
    }

    // Getters
    public Long getId() { return id; }
    public String getCompanyBusinessCode() { return companyBusinessCode; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public String getSupplierCode() { return supplierCode; }
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public Double getTotalAmount() { return totalAmount; }
    public String getNotes() { return notes; }
    public List<DetailResponseDTO> getDetails() { return details; }

    public static class DetailResponseDTO {
        private String itemCode;
        private Double quantity;
        private Double purchasePrice;
        private Double subTotal;

        public DetailResponseDTO(String itemCode, Double quantity, Double purchasePrice, Double subTotal) {
            this.itemCode = itemCode;
            this.quantity = quantity;
            this.purchasePrice = purchasePrice;
            this.subTotal = subTotal;
        }

        public String getItemCode() { return itemCode; }
        public Double getQuantity() { return quantity; }
        public Double getPurchasePrice() { return purchasePrice; }
        public Double getSubTotal() { return subTotal; }
    }
}
