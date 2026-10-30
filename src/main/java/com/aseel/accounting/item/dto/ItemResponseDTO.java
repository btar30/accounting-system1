package com.aseel.accounting.item.dto;

import com.aseel.accounting.item.model.Item;
import java.math.BigDecimal;

public class ItemResponseDTO {

    private Long id;
    private String companyBusinessCode;
    private String itemCode;
    private String nameAr;
    private String nameEn;
    private String unit;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private BigDecimal currentStock;

    // مُنشئ (Constructor) لتحويل الـ Entity مباشرة إلى DTO بكل سهولة
    public ItemResponseDTO(Item item) {
        this.id = item.getId();
        this.companyBusinessCode = item.getCompanyBusinessCode();
        this.itemCode = item.getItemCode();
        this.nameAr = item.getNameAr();
        this.nameEn = item.getNameEn();
        this.unit = item.getUnit();
        this.purchasePrice = item.getPurchasePrice();
        this.salePrice = item.getSalePrice();
        this.currentStock = item.getCurrentStock();
    }

    // Getters
    public Long getId() { return id; }
    public String getCompanyBusinessCode() { return companyBusinessCode; }
    public String getItemCode() { return itemCode; }
    public String getNameAr() { return nameAr; }
    public String getNameEn() { return nameEn; }
    public String getUnit() { return unit; }
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public BigDecimal getSalePrice() { return salePrice; }
    public BigDecimal getCurrentStock() { return currentStock; }
}
