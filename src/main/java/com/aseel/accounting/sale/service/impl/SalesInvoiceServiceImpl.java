package com.aseel.accounting.sale.service.impl;

import com.aseel.accounting.account.model.Account;
import com.aseel.accounting.account.model.Enum.AccountType;
import com.aseel.accounting.account.repository.AccountRepository;
import com.aseel.accounting.item.model.Item;
import com.aseel.accounting.item.repository.ItemRepository;
import com.aseel.accounting.journal.dto.JournalEntryLineRequestDto;
import com.aseel.accounting.journal.dto.JournalEntryRequestDto;
import com.aseel.accounting.journal.service.JournalEntryService;
import com.aseel.accounting.sale.dto.SalesInvoiceRequestDTO;
import com.aseel.accounting.sale.dto.SalesInvoiceResponseDTO;
import com.aseel.accounting.sale.model.SalesInvoice;
import com.aseel.accounting.sale.model.SalesInvoiceDetail;
import com.aseel.accounting.sale.repository.SalesInvoiceRepository;
import com.aseel.accounting.sale.service.SalesInvoiceService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalesInvoiceServiceImpl implements SalesInvoiceService {

    private final SalesInvoiceRepository invoiceRepository;
    private final ItemRepository itemRepository;
    private final AccountRepository accountRepository; // لحفظ وجلب الحسابات المحاسبية
    private final JournalEntryService journalEntryService; // لحفظ القيد التلقائي

    public SalesInvoiceServiceImpl(SalesInvoiceRepository invoiceRepository,
                                   ItemRepository itemRepository,
                                   AccountRepository accountRepository,
                                   JournalEntryService journalEntryService) {
        this.invoiceRepository = invoiceRepository;
        this.itemRepository = itemRepository;
        this.accountRepository = accountRepository;
        this.journalEntryService = journalEntryService;
    }

    @Override
    public List<SalesInvoiceResponseDTO> getInvoicesByCompany(String companyBusinessCode) {
        return invoiceRepository.findByCompanyBusinessCode(companyBusinessCode)
                .stream()
                .map(SalesInvoiceResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SalesInvoiceResponseDTO saveInvoice(SalesInvoiceRequestDTO dto) {
        // 1. توليد رقم فاتورة المبيعات تلقائياً مدمجاً معه كود الشركة
        String generatedInvoiceNumber = generateNextInvoiceNumber(dto.getCompanyBusinessCode());

        SalesInvoice invoice = new SalesInvoice();
        invoice.setCompanyBusinessCode(dto.getCompanyBusinessCode());
        invoice.setInvoiceNumber(generatedInvoiceNumber);
        invoice.setCustomerCode(dto.getCustomerCode());
        invoice.setInvoiceDate(dto.getInvoiceDate());
        invoice.setNotes(dto.getNotes());

        List<SalesInvoiceDetail> detailsList = new ArrayList<>();
        BigDecimal totalInvoiceAmount = BigDecimal.ZERO;

        for (SalesInvoiceRequestDTO.SalesDetailDTO itemDto : dto.getDetails()) {
            Item item = itemRepository.findByCompanyBusinessCodeAndItemCode(dto.getCompanyBusinessCode(), itemDto.getItemCode())
                    .orElseThrow(() -> new RuntimeException("الصنف برمز " + itemDto.getItemCode() + " غير موجود!"));

            BigDecimal currentStock = (item.getCurrentStock() != null) ? item.getCurrentStock() : BigDecimal.ZERO;
            BigDecimal soldQty = (itemDto.getQuantity() != null) ? BigDecimal.valueOf(itemDto.getQuantity()) : BigDecimal.ZERO;
            BigDecimal sPrice = (itemDto.getSalePrice() != null) ? BigDecimal.valueOf(itemDto.getSalePrice()) : BigDecimal.ZERO;

            if (currentStock.compareTo(soldQty) < 0) {
                throw new RuntimeException("الكمية غير متوفرة في المخزون للصنف: " + item.getNameAr());
            }

            BigDecimal subTotal = soldQty.multiply(sPrice);
            totalInvoiceAmount = totalInvoiceAmount.add(subTotal);

            // خصم الكمية المباعة من المخزون الحالي
            item.setCurrentStock(currentStock.subtract(soldQty));
            itemRepository.save(item);

            SalesInvoiceDetail detail = new SalesInvoiceDetail();
            detail.setSalesInvoice(invoice);
            detail.setItemCode(itemDto.getItemCode());
            detail.setQuantity(soldQty.doubleValue());
            detail.setSalePrice(sPrice.doubleValue());
            detail.setSubTotal(subTotal.doubleValue());

            detailsList.add(detail);
        }

        invoice.setDetails(detailsList);
        invoice.setTotalAmount(totalInvoiceAmount.doubleValue());

        // حفظ الفاتورة أولاً
        SalesInvoice savedInvoice = invoiceRepository.save(invoice);

        // ========================================================
        // 2. توليد القيد المحاسبي التلقائي بحالة مسودة (DRAFT) خلف الكواليس
        // ========================================================
        createAutomaticJournalEntryForSale(savedInvoice);

        return new SalesInvoiceResponseDTO(savedInvoice);
    }

    // دالة خاصة لإنشاء القيد الآلي للمبيعات
    private void createAutomaticJournalEntryForSale(SalesInvoice invoice) {
        String companyCode = invoice.getCompanyBusinessCode();
        BigDecimal amount = BigDecimal.valueOf(invoice.getTotalAmount());

        // البحث الآلي عن حساب الإيرادات/المبيعات للشركة (AccountType.REVENUE)
        Account revenueAccount = accountRepository.findAll().stream()
                .filter(acc -> acc.getCompany() != null &&
                        companyCode.equals(acc.getCompany().getBusinessCode()) &&
                        acc.getAccountType() == AccountType.REVENUE &&
                        Boolean.FALSE.equals(acc.getIsParent()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("لا يوجد حساب إيرادات/مبيعات فرعي معرف لهذه الشركة في شجرة الحسابات!"));

        // البحث الآلي عن حساب العملاء أو الأصول المتداولة للشركة
        Account customerAccount = accountRepository.findAll().stream()
                .filter(acc -> acc.getCompany() != null &&
                        companyCode.equals(acc.getCompany().getBusinessCode()) &&
                        acc.getAccountType() == AccountType.ASSET &&
                        Boolean.FALSE.equals(acc.getIsParent()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("لا يوجد حساب عملاء/أصول فرعي معرف لهذه الشركة في شجرة الحسابات!"));

        // سطر المدين (حساب العملاء)
        JournalEntryLineRequestDto debitLine = JournalEntryLineRequestDto.builder()
                .accountId(customerAccount.getId())
                .debit(amount)
                .credit(BigDecimal.ZERO)
                .description("قيد مبيعات آلي للفاتورة: " + invoice.getInvoiceNumber() + " - العميل: " + invoice.getCustomerCode())
                .build();

        // سطر الدائن (حساب الإيرادات/المبيعات)
        JournalEntryLineRequestDto creditLine = JournalEntryLineRequestDto.builder()
                .accountId(revenueAccount.getId())
                .debit(BigDecimal.ZERO)
                .credit(amount)
                .description("قيد مبيعات آلي للفاتورة: " + invoice.getInvoiceNumber())
                .build();

        // تجهيز طلب القيد المحاسبي
        JournalEntryRequestDto entryRequest = JournalEntryRequestDto.builder()
                .companyBusinessCode(companyCode)
                .entryDate(invoice.getInvoiceDate())
                .description("قيد مبيعات تلقائي للفاتورة رقم: " + invoice.getInvoiceNumber())
                .lines(List.of(debitLine, creditLine))
                .build();

        // إرسال الطلب لخدمة القيود (سيتم حفظه بحالة DRAFT تلقائياً)
        journalEntryService.createJournalEntry(entryRequest);
    }

    private String generateNextInvoiceNumber(String companyCode) {
        Pageable pageable = PageRequest.of(0, 1);
        List<String> lastInvoiceNums = invoiceRepository.findLastInvoiceNumbersByCompany(companyCode, pageable);

        String companyPrefix = "SINV-" + companyCode + "-";

        if (lastInvoiceNums == null || lastInvoiceNums.isEmpty()) {
            return companyPrefix + "1001";
        }
        try {
            String lastInvoiceNum = lastInvoiceNums.get(0);
            String numberPart = lastInvoiceNum.substring(lastInvoiceNum.lastIndexOf("-") + 1);
            int nextNum = Integer.parseInt(numberPart) + 1;
            return companyPrefix + nextNum;
        } catch (Exception e) {
            return companyPrefix + System.currentTimeMillis();
        }
    }
}
