package com.aseel.accounting.purchase.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "purchase_invoices")
public class PurchaseInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyBusinessCode;

    @Column(nullable = false, unique = true)
    private String invoiceNumber; // رقم الفاتورة التلقائي (مثال: PINV-2026-0001)

    @Column(nullable = false)
    private String supplierCode; // كود المورد الذي اشترينا منه

    @Column(nullable = false)
    private LocalDate invoiceDate; // تاريخ الفاتورة

    private Double totalAmount; // إجمالي مبلغ الفاتورة

    private String notes; // ملاحظات

    @OneToMany(mappedBy = "purchaseInvoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseInvoiceDetail> details; // تفاصيل الأصناف في الفاتورة

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyBusinessCode() { return companyBusinessCode; }
    public void setCompanyBusinessCode(String companyBusinessCode) { this.companyBusinessCode = companyBusinessCode; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }

    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<PurchaseInvoiceDetail> getDetails() { return details; }
    public void setDetails(List<PurchaseInvoiceDetail> details) { this.details = details; }
}
