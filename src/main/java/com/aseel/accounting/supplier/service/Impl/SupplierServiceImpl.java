package com.aseel.accounting.supplier.service.Impl;

import com.aseel.accounting.supplier.dto.SupplierRequestDTO;
import com.aseel.accounting.supplier.dto.SupplierResponseDTO;
import com.aseel.accounting.supplier.model.Supplier;
import com.aseel.accounting.supplier.repository.SupplierRepository;
import com.aseel.accounting.supplier.service.SupplierService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public List<SupplierResponseDTO> getSuppliersByCompany(String companyBusinessCode) {
        return supplierRepository.findByCompanyBusinessCode(companyBusinessCode)
                .stream()
                .map(SupplierResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public SupplierResponseDTO getSupplierByCode(String companyBusinessCode, String supplierCode) {
        Supplier supplier = supplierRepository.findByCompanyBusinessCodeAndSupplierCode(companyBusinessCode, supplierCode)
                .orElseThrow(() -> new RuntimeException("المورد غير موجود!"));
        return new SupplierResponseDTO(supplier);
    }

    @Override
    @Transactional
    public SupplierResponseDTO saveSupplier(SupplierRequestDTO dto) {
        // ===== توليد الكود تلقائياً إذا لم يتم إرساله =====
        if (dto.getSupplierCode() == null || dto.getSupplierCode().isEmpty()) {
            dto.setSupplierCode(generateSupplierCode(dto.getCompanyBusinessCode()));
        }

        // التحقق من عدم تكرار الكود
        supplierRepository.findByCompanyBusinessCodeAndSupplierCode(dto.getCompanyBusinessCode(), dto.getSupplierCode())
                .ifPresent(existing -> {
                    throw new RuntimeException("كود المورد مستخدم مسبقاً لهذه الشركة!");
                });

        Supplier supplier = new Supplier();
        supplier.setCompanyBusinessCode(dto.getCompanyBusinessCode());
        supplier.setSupplierCode(dto.getSupplierCode());
        supplier.setName(dto.getName());
        supplier.setPhone(dto.getPhone());
        supplier.setAddress(dto.getAddress());
        supplier.setNotes(dto.getNotes());

        Supplier savedSupplier = supplierRepository.save(supplier);
        return new SupplierResponseDTO(savedSupplier);
    }

    @Override
    @Transactional
    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("المورد المراد حذفه غير موجود!");
        }
        supplierRepository.deleteById(id);
    }

    // ===== دالة توليد الكود التلقائي (باستخدام Pageable) =====
    private String generateSupplierCode(String companyCode) {
        try {
            // استخدام Pageable لجلب أول نتيجة فقط
            Pageable pageable = PageRequest.of(0, 1);
            List<String> lastCodes = supplierRepository.findLastSupplierCodeByCompany(companyCode, pageable);

            // إذا لم يوجد أي كود، نبدأ من SUP-1001
            if (lastCodes.isEmpty()) {
                return "SUP-1001";
            }

            String code = lastCodes.get(0);

            // التحقق من أن الكود يبدأ بـ SUP-
            if (code == null || code.isEmpty() || !code.startsWith("SUP-")) {
                return "SUP-1001";
            }

            // استخراج الجزء الرقمي من الكود
            String numberPart = code.replace("SUP-", "").trim();

            // التحقق من أن الجزء الرقمي صحيح
            if (numberPart.isEmpty()) {
                return "SUP-1001";
            }

            int nextNum = Integer.parseInt(numberPart) + 1;
            return "SUP-" + nextNum;

        } catch (NumberFormatException e) {
            // في حالة وجود خطأ في التنسيق، نستخدم الوقت الحالي
            return "SUP-" + System.currentTimeMillis();
        } catch (Exception e) {
            return "SUP-" + System.currentTimeMillis();
        }
    }
}