package com.aseel.accounting.customers.service.impl;

import com.aseel.accounting.customers.dto.CustomerRequestDTO;
import com.aseel.accounting.customers.dto.CustomerResponseDTO;
import com.aseel.accounting.customers.model.Customer;
import com.aseel.accounting.customers.repository.CustomerRepository;
import com.aseel.accounting.customers.service.CustomerService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<CustomerResponseDTO> getCustomersByCompany(String companyBusinessCode) {
        return customerRepository.findByCompanyBusinessCode(companyBusinessCode)
                .stream()
                .map(CustomerResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponseDTO getCustomerByCode(String companyBusinessCode, String customerCode) {
        Customer customer = customerRepository.findByCompanyBusinessCodeAndCustomerCode(companyBusinessCode, customerCode)
                .orElseThrow(() -> new RuntimeException("العميل غير موجود!"));
        return new CustomerResponseDTO(customer);
    }

    @Override
    @Transactional
    public CustomerResponseDTO saveCustomer(CustomerRequestDTO dto) {
        // ===== توليد الكود تلقائياً إذا لم يتم إرساله =====
        if (dto.getCustomerCode() == null || dto.getCustomerCode().isEmpty()) {
            dto.setCustomerCode(generateCustomerCode(dto.getCompanyBusinessCode()));
        }

        // التحقق من عدم تكرار الكود
        customerRepository.findByCompanyBusinessCodeAndCustomerCode(dto.getCompanyBusinessCode(), dto.getCustomerCode())
                .ifPresent(existing -> {
                    throw new RuntimeException("كود العميل مستخدم مسبقاً لهذه الشركة!");
                });

        Customer customer = new Customer();
        customer.setCompanyBusinessCode(dto.getCompanyBusinessCode());
        customer.setCustomerCode(dto.getCustomerCode());
        customer.setName(dto.getName());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setNotes(dto.getNotes());

        Customer savedCustomer = customerRepository.save(customer);
        return new CustomerResponseDTO(savedCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("العميل المراد حذفه غير موجود!");
        }
        customerRepository.deleteById(id);
    }

    // ===== دالة توليد الكود التلقائي (باستخدام Pageable) =====
    private String generateCustomerCode(String companyCode) {
        try {
            // استخدام Pageable لجلب أول نتيجة فقط
            Pageable pageable = PageRequest.of(0, 1);
            List<String> lastCodes = customerRepository.findLastCustomerCodeByCompany(companyCode, pageable);

            // إذا لم يوجد أي كود، نبدأ من CUST-1001
            if (lastCodes.isEmpty()) {
                return "CUST-1001";
            }

            String code = lastCodes.get(0);

            // التحقق من أن الكود يبدأ بـ CUST-
            if (code == null || code.isEmpty() || !code.startsWith("CUST-")) {
                return "CUST-1001";
            }

            // استخراج الجزء الرقمي من الكود
            String numberPart = code.replace("CUST-", "").trim();

            // التحقق من أن الجزء الرقمي صحيح
            if (numberPart.isEmpty()) {
                return "CUST-1001";
            }

            int nextNum = Integer.parseInt(numberPart) + 1;
            return "CUST-" + nextNum;

        } catch (NumberFormatException e) {
            // في حالة وجود خطأ في التنسيق، نستخدم الوقت الحالي
            return "CUST-" + System.currentTimeMillis();
        } catch (Exception e) {
            return "CUST-" + System.currentTimeMillis();
        }
    }
}