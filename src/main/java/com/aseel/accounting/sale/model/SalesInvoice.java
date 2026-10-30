package com.aseel.accounting.sale.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "sales_invoices")
public class SalesInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyBusinessCode;

    @Column(nullable = false, unique = true)
    private String invoiceNumber; // رقم فاتورة المبيعات التلقائي (مثال: SINV-1001)

    @Column(nullable = false)
    private String customerCode; // كود العميل الذي اشترينا له

    @Column(nullable = false)
    private LocalDate invoiceDate; // تاريخ الفاتورة

    private Double totalAmount; // إجمالي مبلغ الفاتورة

    private String notes; // ملاحظات

    @OneToMany(mappedBy = "salesInvoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalesInvoiceDetail> details; // تفاصيل الأصناف المباعة

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyBusinessCode() { return companyBusinessCode; }
    public void setCompanyBusinessCode(String companyBusinessCode) { this.companyBusinessCode = companyBusinessCode; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }

    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<SalesInvoiceDetail> getDetails() { return details; }
    public void setDetails(List<SalesInvoiceDetail> details) { this.details = details; }
}
