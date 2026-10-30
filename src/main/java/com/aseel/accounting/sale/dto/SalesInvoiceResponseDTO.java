package com.aseel.accounting.sale.dto;

import com.aseel.accounting.sale.model.SalesInvoice;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class SalesInvoiceResponseDTO {
    private Long id;
    private String companyBusinessCode;
    private String invoiceNumber;
    private String customerCode;
    private LocalDate invoiceDate;
    private Double totalAmount;
    private String notes;
    private List<DetailResponseDTO> details;

    public SalesInvoiceResponseDTO(SalesInvoice invoice) {
        this.id = invoice.getId();
        this.companyBusinessCode = invoice.getCompanyBusinessCode();
        this.invoiceNumber = invoice.getInvoiceNumber();
        this.customerCode = invoice.getCustomerCode();
        this.invoiceDate = invoice.getInvoiceDate();
        this.totalAmount = invoice.getTotalAmount();
        this.notes = invoice.getNotes();
        if (invoice.getDetails() != null) {
            this.details = invoice.getDetails().stream()
                    .map(d -> new DetailResponseDTO(d.getItemCode(), d.getQuantity(), d.getSalePrice(), d.getSubTotal()))
                    .collect(Collectors.toList());
        }
    }

    // Getters
    public Long getId() { return id; }
    public String getCompanyBusinessCode() { return companyBusinessCode; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public String getCustomerCode() { return customerCode; }
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public Double getTotalAmount() { return totalAmount; }
    public String getNotes() { return notes; }
    public List<DetailResponseDTO> getDetails() { return details; }

    public static class DetailResponseDTO {
        private String itemCode;
        private Double quantity;
        private Double salePrice;
        private Double subTotal;

        public DetailResponseDTO(String itemCode, Double quantity, Double salePrice, Double subTotal) {
            this.itemCode = itemCode;
            this.quantity = quantity;
            this.salePrice = salePrice;
            this.subTotal = subTotal;
        }

        public String getItemCode() { return itemCode; }
        public Double getQuantity() { return quantity; }
        public Double getSalePrice() { return salePrice; }
        public Double getSubTotal() { return subTotal; }
    }
}
