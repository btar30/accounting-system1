package com.aseel.accounting.item.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "items", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"companyBusinessCode", "itemCode"})
})
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyBusinessCode; // لربط الصنف بالشركة باستخدام كود الشركة

    @Column(nullable = false)
    private String itemCode; // كود أو باركود الصنف

    @Column(nullable = false, length = 150)
    private String nameAr; // اسم الصنف بالعربية

    @Column(length = 150)
    private String nameEn; // اسم الصنف بالإنجليزية

    @Column(nullable = false)
    private String unit = "حبة"; // وحدة القياس (حبة، كرتون، كيلو...)

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal purchasePrice = BigDecimal.ZERO; // سعر الشراء (التكلفة)

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal salePrice = BigDecimal.ZERO; // سعر البيع

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal currentStock = BigDecimal.ZERO; // الكمية الحالية في المخزن

    // بناء فارغ مطلوب بواسطة JPA
    public Item() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyBusinessCode() { return companyBusinessCode; }
    public void setCompanyBusinessCode(String companyBusinessCode) { this.companyBusinessCode = companyBusinessCode; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getNameAr() { return nameAr; }
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }

    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }

    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }

    public BigDecimal getCurrentStock() { return currentStock; }
    public void setCurrentStock(BigDecimal currentStock) { this.currentStock = currentStock; }
}
