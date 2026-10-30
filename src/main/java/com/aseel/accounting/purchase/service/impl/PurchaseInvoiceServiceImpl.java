////package com.aseel.accounting.purchase.service.impl;
////
////import com.aseel.accounting.item.model.Item;
////import com.aseel.accounting.item.repository.ItemRepository;
////import com.aseel.accounting.purchase.dto.PurchaseInvoiceRequestDTO;
////import com.aseel.accounting.purchase.dto.PurchaseInvoiceResponseDTO;
////import com.aseel.accounting.purchase.model.PurchaseInvoice;
////import com.aseel.accounting.purchase.model.PurchaseInvoiceDetail;
////import com.aseel.accounting.purchase.repository.PurchaseInvoiceRepository;
////import com.aseel.accounting.purchase.service.PurchaseInvoiceService;
////import org.springframework.stereotype.Service;
////import org.springframework.transaction.annotation.Transactional;
////
////import java.math.BigDecimal;
////import java.util.ArrayList;
////import java.util.List;
////import java.util.stream.Collectors;
////
////@Service
////public class PurchaseInvoiceServiceImpl implements PurchaseInvoiceService {
////
////    private final PurchaseInvoiceRepository invoiceRepository;
////    private final ItemRepository itemRepository;
////
////    public PurchaseInvoiceServiceImpl(PurchaseInvoiceRepository invoiceRepository, ItemRepository itemRepository) {
////        this.invoiceRepository = invoiceRepository;
////        this.itemRepository = itemRepository;
////    }
////
////    @Override
////    public List<PurchaseInvoiceResponseDTO> getInvoicesByCompany(String companyBusinessCode) {
////        return invoiceRepository.findByCompanyBusinessCode(companyBusinessCode)
////                .stream()
////                .map(PurchaseInvoiceResponseDTO::new)
////                .collect(Collectors.toList());
////    }
////
////    @Override
////    @Transactional
////    public PurchaseInvoiceResponseDTO saveInvoice(PurchaseInvoiceRequestDTO dto) {
////        // 1. توليد رقم الفاتورة تلقائياً
////        String generatedInvoiceNumber = generateNextInvoiceNumber(dto.getCompanyBusinessCode());
////
////        PurchaseInvoice invoice = new PurchaseInvoice();
////        invoice.setCompanyBusinessCode(dto.getCompanyBusinessCode());
////        invoice.setInvoiceNumber(generatedInvoiceNumber);
////        invoice.setSupplierCode(dto.getSupplierCode());
////        invoice.setInvoiceDate(dto.getInvoiceDate());
////        invoice.setNotes(dto.getNotes());
////
////        List<PurchaseInvoiceDetail> detailsList = new ArrayList<>();
////        BigDecimal totalInvoiceAmount = BigDecimal.ZERO;
////
////        for (PurchaseInvoiceRequestDTO.PurchaseDetailDTO itemDto : dto.getDetails()) {
////            // التحقق من وجود الصنف وتحديث مخزنه
////            Item item = itemRepository.findByCompanyBusinessCodeAndItemCode(dto.getCompanyBusinessCode(), itemDto.getItemCode())
////                    .orElseThrow(() -> new RuntimeException("الصنف برمز " + itemDto.getItemCode() + " غير موجود!"));
////
////            // التعامل الآمن مع الـ BigDecimal لتجنب NullPointerException
////            BigDecimal currentStock = (item.getCurrentStock() != null) ? item.getCurrentStock() : BigDecimal.ZERO;
////            BigDecimal addedQty = (itemDto.getQuantity() != null) ? BigDecimal.valueOf(itemDto.getQuantity()) : BigDecimal.ZERO;
////            BigDecimal purPrice = (itemDto.getPurchasePrice() != null) ? BigDecimal.valueOf(itemDto.getPurchasePrice()) : BigDecimal.ZERO;
////
////            // حساب الإجمالي الفرعي للصنف (الكمية × السعر)
////            BigDecimal subTotal = addedQty.multiply(purPrice);
////            totalInvoiceAmount = totalInvoiceAmount.add(subTotal);
////
////            // تحديث الكمية في المخزون وسعر الشراء الصنف
////            item.setCurrentStock(currentStock.add(addedQty));
////            item.setPurchasePrice(purPrice);
////            itemRepository.save(item);
////
////            // إنشاء تفصيل الفاتورة
////            PurchaseInvoiceDetail detail = new PurchaseInvoiceDetail();
////            detail.setPurchaseInvoice(invoice);
////            detail.setItemCode(itemDto.getItemCode());
////            detail.setQuantity(addedQty.doubleValue());
////            detail.setPurchasePrice(purPrice.doubleValue());
////            detail.setSubTotal(subTotal.doubleValue());
////
////            detailsList.add(detail);
////        }
////
////        invoice.setDetails(detailsList);
////        invoice.setTotalAmount(totalInvoiceAmount.doubleValue());
////
////        PurchaseInvoice savedInvoice = invoiceRepository.save(invoice);
////        return new PurchaseInvoiceResponseDTO(savedInvoice);
////    }
////
////    // دالة مساعدة لتوليد كود الفاتورة التلقائي المسلسل
////    private String generateNextInvoiceNumber(String companyCode) {
////        String lastInvoiceNum = invoiceRepository.findLastInvoiceNumber(companyCode);
////        if (lastInvoiceNum == null || lastInvoiceNum.isEmpty()) {
////            return "PINV-1001"; // البداية لو كانت أول فاتورة
////        }
////        try {
////            String numberPart = lastInvoiceNum.replace("PINV-", "");
////            int nextNum = Integer.parseInt(numberPart) + 1;
////            return "PINV-" + nextNum;
////        } catch (Exception e) {
////            return "PINV-" + System.currentTimeMillis();
////        }
////    }
////}
////
//
//
//
//
//
//package com.aseel.accounting.purchase.service.impl;
//
//import com.aseel.accounting.account.model.Account;
//import com.aseel.accounting.account.model.Enum.AccountType;
//import com.aseel.accounting.account.repository.AccountRepository;
//import com.aseel.accounting.item.model.Item;
//import com.aseel.accounting.item.repository.ItemRepository;
//import com.aseel.accounting.journal.dto.JournalEntryLineRequestDto;
//import com.aseel.accounting.journal.dto.JournalEntryRequestDto;
//import com.aseel.accounting.journal.service.JournalEntryService;
//import com.aseel.accounting.purchase.dto.PurchaseInvoiceRequestDTO;
//import com.aseel.accounting.purchase.dto.PurchaseInvoiceResponseDTO;
//import com.aseel.accounting.purchase.model.PurchaseInvoice;
//import com.aseel.accounting.purchase.model.PurchaseInvoiceDetail;
//import com.aseel.accounting.purchase.repository.PurchaseInvoiceRepository;
//import com.aseel.accounting.purchase.service.PurchaseInvoiceService;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class PurchaseInvoiceServiceImpl implements PurchaseInvoiceService {
//
//    private final PurchaseInvoiceRepository invoiceRepository;
//    private final ItemRepository itemRepository;
//    private final AccountRepository accountRepository; // لحفظ وجلب الحسابات المحاسبية
//    private final JournalEntryService journalEntryService; // لحفظ القيد التلقائي
//
//    public PurchaseInvoiceServiceImpl(PurchaseInvoiceRepository invoiceRepository,
//                                      ItemRepository itemRepository,
//                                      AccountRepository accountRepository,
//                                      JournalEntryService journalEntryService) {
//        this.invoiceRepository = invoiceRepository;
//        this.itemRepository = itemRepository;
//        this.accountRepository = accountRepository;
//        this.journalEntryService = journalEntryService;
//    }
//
//    @Override
//    public List<PurchaseInvoiceResponseDTO> getInvoicesByCompany(String companyBusinessCode) {
//        return invoiceRepository.findByCompanyBusinessCode(companyBusinessCode)
//                .stream()
//                .map(PurchaseInvoiceResponseDTO::new)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional
//    public PurchaseInvoiceResponseDTO saveInvoice(PurchaseInvoiceRequestDTO dto) {
//        // 1. توليد رقم الفاتورة تلقائياً
//        String generatedInvoiceNumber = generateNextInvoiceNumber(dto.getCompanyBusinessCode());
//
//        PurchaseInvoice invoice = new PurchaseInvoice();
//        invoice.setCompanyBusinessCode(dto.getCompanyBusinessCode());
//        invoice.setInvoiceNumber(generatedInvoiceNumber);
//        invoice.setSupplierCode(dto.getSupplierCode());
//        invoice.setInvoiceDate(dto.getInvoiceDate());
//        invoice.setNotes(dto.getNotes());
//
//        List<PurchaseInvoiceDetail> detailsList = new ArrayList<>();
//        BigDecimal totalInvoiceAmount = BigDecimal.ZERO;
//
//        for (PurchaseInvoiceRequestDTO.PurchaseDetailDTO itemDto : dto.getDetails()) {
//            Item item = itemRepository.findByCompanyBusinessCodeAndItemCode(dto.getCompanyBusinessCode(), itemDto.getItemCode())
//                    .orElseThrow(() -> new RuntimeException("الصنف برمز " + itemDto.getItemCode() + " غير موجود!"));
//
//            BigDecimal currentStock = (item.getCurrentStock() != null) ? item.getCurrentStock() : BigDecimal.ZERO;
//            BigDecimal addedQty = (itemDto.getQuantity() != null) ? BigDecimal.valueOf(itemDto.getQuantity()) : BigDecimal.ZERO;
//            BigDecimal purPrice = (itemDto.getPurchasePrice() != null) ? BigDecimal.valueOf(itemDto.getPurchasePrice()) : BigDecimal.ZERO;
//
//            BigDecimal subTotal = addedQty.multiply(purPrice);
//            totalInvoiceAmount = totalInvoiceAmount.add(subTotal);
//
//            // تحديث كمية المخزون وسعر الشراء
//            item.setCurrentStock(currentStock.add(addedQty));
//            item.setPurchasePrice(purPrice);
//            itemRepository.save(item);
//
//            PurchaseInvoiceDetail detail = new PurchaseInvoiceDetail();
//            detail.setPurchaseInvoice(invoice);
//            detail.setItemCode(itemDto.getItemCode());
//            detail.setQuantity(addedQty.doubleValue());
//            detail.setPurchasePrice(purPrice.doubleValue());
//            detail.setSubTotal(subTotal.doubleValue());
//
//            detailsList.add(detail);
//        }
//
//        invoice.setDetails(detailsList);
//        invoice.setTotalAmount(totalInvoiceAmount.doubleValue());
//
//        // حفظ الفاتورة أولاً
//        PurchaseInvoice savedInvoice = invoiceRepository.save(invoice);
//
//        // ========================================================
//        // 2. توليد القيد المحاسبي التلقائي بحالة مسودة (DRAFT) للمشتريات
//        // ========================================================
//        createAutomaticJournalEntryForPurchase(savedInvoice);
//
//        return new PurchaseInvoiceResponseDTO(savedInvoice);
//    }
//
//    // دالة خاصة لإنشاء القيد الآلي للمشتريات
//    private void createAutomaticJournalEntryForPurchase(PurchaseInvoice invoice) {
//        String companyCode = invoice.getCompanyBusinessCode();
//        BigDecimal amount = BigDecimal.valueOf(invoice.getTotalAmount());
//
//        // البحث الآلي عن حساب المخزون/الأصول للشركة (AccountType.ASSET)
//        Account inventoryAccount = accountRepository.findAll().stream()
//                .filter(acc -> acc.getCompany() != null &&
//                        companyCode.equals(acc.getCompany().getBusinessCode()) &&
//                        acc.getAccountType() == AccountType.ASSET &&
//                        Boolean.FALSE.equals(acc.getIsParent()))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("لا يوجد حساب مخزون/أصول فرعي معرف لهذه الشركة في شجرة الحسابات!"));
//
//        // البحث الآلي عن حساب الموردين/الالتزامات للشركة (AccountType.LIABILITY)
//        Account supplierAccount = accountRepository.findAll().stream()
//                .filter(acc -> acc.getCompany() != null &&
//                        companyCode.equals(acc.getCompany().getBusinessCode()) &&
//                        acc.getAccountType() == AccountType.LIABILITY &&
//                        Boolean.FALSE.equals(acc.getIsParent()))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("لا يوجد حساب موردين/التزامات فرعي معرف لهذه الشركة في شجرة الحسابات!"));
//
//        // سطر المدين (حساب المخزون / زيادة الأصول)
//        JournalEntryLineRequestDto debitLine = JournalEntryLineRequestDto.builder()
//                .accountId(inventoryAccount.getId())
//                .debit(amount)
//                .credit(BigDecimal.ZERO)
//                .description("قيد مشريات آلي للفاتورة: " + invoice.getInvoiceNumber())
//                .build();
//
//        // سطر الدائن (حساب الموردين / الالتزامات)
//        JournalEntryLineRequestDto creditLine = JournalEntryLineRequestDto.builder()
//                .accountId(supplierAccount.getId())
//                .debit(BigDecimal.ZERO)
//                .credit(amount)
//                .description("قيد مشريات آلي للفاتورة: " + invoice.getInvoiceNumber() + " - المورد: " + invoice.getSupplierCode())
//                .build();
//
//        // تجهيز طلب القيد المحاسبي
//        JournalEntryRequestDto entryRequest = JournalEntryRequestDto.builder()
//                .companyBusinessCode(companyCode)
//                .entryDate(invoice.getInvoiceDate())
//                .description("قيد مشريات تلقائي للفاتورة رقم: " + invoice.getInvoiceNumber())
//                .lines(List.of(debitLine, creditLine))
//                .build();
//
//        // إرسال الطلب لخدمة القيود (سيحفظ بحالة DRAFT تلقائياً)
//        journalEntryService.createJournalEntry(entryRequest);
//    }
//
//    private String generateNextInvoiceNumber(String companyCode) {
//        String lastInvoiceNum = invoiceRepository.findLastInvoiceNumber(companyCode);
//        if (lastInvoiceNum == null || lastInvoiceNum.isEmpty()) {
//            return "PINV-1001";
//        }
//        try {
//            String numberPart = lastInvoiceNum.replace("PINV-", "");
//            int nextNum = Integer.parseInt(numberPart) + 1;
//            return "PINV-" + nextNum;
//        } catch (Exception e) {
//            return "PINV-" + System.currentTimeMillis();
//        }
//    }
//}
//
//
//


package com.aseel.accounting.purchase.service.impl;

import com.aseel.accounting.account.model.Account;
import com.aseel.accounting.account.model.Enum.AccountType;
import com.aseel.accounting.account.repository.AccountRepository;
import com.aseel.accounting.item.model.Item;
import com.aseel.accounting.item.repository.ItemRepository;
import com.aseel.accounting.journal.dto.JournalEntryLineRequestDto;
import com.aseel.accounting.journal.dto.JournalEntryRequestDto;
import com.aseel.accounting.journal.service.JournalEntryService;
import com.aseel.accounting.purchase.dto.PurchaseInvoiceRequestDTO;
import com.aseel.accounting.purchase.dto.PurchaseInvoiceResponseDTO;
import com.aseel.accounting.purchase.model.PurchaseInvoice;
import com.aseel.accounting.purchase.model.PurchaseInvoiceDetail;
import com.aseel.accounting.purchase.repository.PurchaseInvoiceRepository;
import com.aseel.accounting.purchase.service.PurchaseInvoiceService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseInvoiceServiceImpl implements PurchaseInvoiceService {

    private final PurchaseInvoiceRepository invoiceRepository;
    private final ItemRepository itemRepository;
    private final AccountRepository accountRepository;
    private final JournalEntryService journalEntryService;

    public PurchaseInvoiceServiceImpl(PurchaseInvoiceRepository invoiceRepository,
                                      ItemRepository itemRepository,
                                      AccountRepository accountRepository,
                                      JournalEntryService journalEntryService) {
        this.invoiceRepository = invoiceRepository;
        this.itemRepository = itemRepository;
        this.accountRepository = accountRepository;
        this.journalEntryService = journalEntryService;
    }

    @Override
    public List<PurchaseInvoiceResponseDTO> getInvoicesByCompany(String companyBusinessCode) {
        return invoiceRepository.findByCompanyBusinessCode(companyBusinessCode)
                .stream()
                .map(PurchaseInvoiceResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PurchaseInvoiceResponseDTO saveInvoice(PurchaseInvoiceRequestDTO dto) {
        // 1. توليد رقم الفاتورة مدمجاً معه كود الشركة ليكون فريداً كلياً
        String generatedInvoiceNumber = generateNextInvoiceNumber(dto.getCompanyBusinessCode());

        PurchaseInvoice invoice = new PurchaseInvoice();
        invoice.setCompanyBusinessCode(dto.getCompanyBusinessCode());
        invoice.setInvoiceNumber(generatedInvoiceNumber);
        invoice.setSupplierCode(dto.getSupplierCode());
        invoice.setInvoiceDate(dto.getInvoiceDate());
        invoice.setNotes(dto.getNotes());

        List<PurchaseInvoiceDetail> detailsList = new ArrayList<>();
        BigDecimal totalInvoiceAmount = BigDecimal.ZERO;

        for (PurchaseInvoiceRequestDTO.PurchaseDetailDTO itemDto : dto.getDetails()) {
            Item item = itemRepository.findByCompanyBusinessCodeAndItemCode(dto.getCompanyBusinessCode(), itemDto.getItemCode())
                    .orElseThrow(() -> new RuntimeException("الصنف برمز " + itemDto.getItemCode() + " غير موجود!"));

            BigDecimal currentStock = (item.getCurrentStock() != null) ? item.getCurrentStock() : BigDecimal.ZERO;
            BigDecimal addedQty = (itemDto.getQuantity() != null) ? BigDecimal.valueOf(itemDto.getQuantity()) : BigDecimal.ZERO;
            BigDecimal purPrice = (itemDto.getPurchasePrice() != null) ? BigDecimal.valueOf(itemDto.getPurchasePrice()) : BigDecimal.ZERO;

            BigDecimal subTotal = addedQty.multiply(purPrice);
            totalInvoiceAmount = totalInvoiceAmount.add(subTotal);

            item.setCurrentStock(currentStock.add(addedQty));
            item.setPurchasePrice(purPrice);
            itemRepository.save(item);

            PurchaseInvoiceDetail detail = new PurchaseInvoiceDetail();
            detail.setPurchaseInvoice(invoice);
            detail.setItemCode(itemDto.getItemCode());
            detail.setQuantity(addedQty.doubleValue());
            detail.setPurchasePrice(purPrice.doubleValue());
            detail.setSubTotal(subTotal.doubleValue());

            detailsList.add(detail);
        }

        invoice.setDetails(detailsList);
        invoice.setTotalAmount(totalInvoiceAmount.doubleValue());

        PurchaseInvoice savedInvoice = invoiceRepository.save(invoice);

        createAutomaticJournalEntryForPurchase(savedInvoice);

        return new PurchaseInvoiceResponseDTO(savedInvoice);
    }

    private void createAutomaticJournalEntryForPurchase(PurchaseInvoice invoice) {
        String companyCode = invoice.getCompanyBusinessCode();
        BigDecimal amount = BigDecimal.valueOf(invoice.getTotalAmount());

        Account inventoryAccount = accountRepository.findAll().stream()
                .filter(acc -> acc.getCompany() != null &&
                        companyCode.equals(acc.getCompany().getBusinessCode()) &&
                        acc.getAccountType() == AccountType.ASSET &&
                        Boolean.FALSE.equals(acc.getIsParent()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("لا يوجد حساب مخزون/أصول فرعي معرف لهذه الشركة في شجرة الحسابات!"));

        Account supplierAccount = accountRepository.findAll().stream()
                .filter(acc -> acc.getCompany() != null &&
                        companyCode.equals(acc.getCompany().getBusinessCode()) &&
                        acc.getAccountType() == AccountType.LIABILITY &&
                        Boolean.FALSE.equals(acc.getIsParent()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("لا يوجد حساب موردين/التزامات فرعي معرف لهذه الشركة في شجرة الحسابات!"));

        JournalEntryLineRequestDto debitLine = JournalEntryLineRequestDto.builder()
                .accountId(inventoryAccount.getId())
                .debit(amount)
                .credit(BigDecimal.ZERO)
                .description("قيد مشريات آلي للفاتورة: " + invoice.getInvoiceNumber())
                .build();

        JournalEntryLineRequestDto creditLine = JournalEntryLineRequestDto.builder()
                .accountId(supplierAccount.getId())
                .debit(BigDecimal.ZERO)
                .credit(amount)
                .description("قيد مشريات آلي للفاتورة: " + invoice.getInvoiceNumber() + " - المورد: " + invoice.getSupplierCode())
                .build();

        JournalEntryRequestDto entryRequest = JournalEntryRequestDto.builder()
                .companyBusinessCode(companyCode)
                .entryDate(invoice.getInvoiceDate())
                .description("قيد مشريات تلقائي للفاتورة رقم: " + invoice.getInvoiceNumber())
                .lines(List.of(debitLine, creditLine))
                .build();

        journalEntryService.createJournalEntry(entryRequest);
    }

    // دالة توليد رقم الفاتورة بحيث يحتوي على كود الشركة لتفادي التكرار نهائياً
    private String generateNextInvoiceNumber(String companyCode) {
        Pageable pageable = PageRequest.of(0, 1);
        List<String> lastInvoiceNums = invoiceRepository.findLastInvoiceNumbersByCompany(companyCode, pageable);

        // استخراج بادئة فريدة من كود الشركة (مثلاً آخر 6 أحرف أو الكود كاملاً)
        String companyPrefix = "PINV-" + companyCode + "-";

        if (lastInvoiceNums == null || lastInvoiceNums.isEmpty()) {
            return companyPrefix + "1001";
        }
        try {
            String lastInvoiceNum = lastInvoiceNums.get(0);
            // استخراج الرقم المسلسل الأخير
            String numberPart = lastInvoiceNum.substring(lastInvoiceNum.lastIndexOf("-") + 1);
            int nextNum = Integer.parseInt(numberPart) + 1;
            return companyPrefix + nextNum;
        } catch (Exception e) {
            return companyPrefix + System.currentTimeMillis();
        }
    }
}
