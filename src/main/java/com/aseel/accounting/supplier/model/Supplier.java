package com.aseel.accounting.supplier.model;

import jakarta.persistence.*;

@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyBusinessCode; // كود الشركة المالكة للنظام (نفس طريقة الأصناف)

    @Column(nullable = false)
    private String supplierCode; // كود المورد الخاص بالشركة

    @Column(nullable = false)
    private String name; // اسم المورد (اسم شركة الأدوية أو الموزع)

    private String phone; // رقم الهاتف

    private String address; // العنوان

    private String notes; // ملاحظات

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyBusinessCode() { return companyBusinessCode; }
    public void setCompanyBusinessCode(String companyBusinessCode) { this.companyBusinessCode = companyBusinessCode; }

    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
