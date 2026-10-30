package com.aseel.accounting.account.service.impl;

import com.aseel.accounting.account.dto.AccountRequestDto;
import com.aseel.accounting.account.dto.AccountResponseDto;
import com.aseel.accounting.account.dto.AccountTreeDto;
import com.aseel.accounting.account.model.Account;
import com.aseel.accounting.account.model.Enum.AccountType;
import com.aseel.accounting.account.repository.AccountRepository;
import com.aseel.accounting.account.service.AccountService;
import com.aseel.accounting.company.model.Company;
import com.aseel.accounting.company.repository.CompanyRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CompanyRepository companyRepository;

    //========================================================
    // 1. إنشاء حساب جديد
    //========================================================
    @Override
    @Transactional
    public AccountResponseDto createAccount(AccountRequestDto dto) {
        // ---  هنا: البحث عن الشركة بواسطة businessCode ---
        // 1. التحقق من وجود الشركة باستعمال الكود
        Company company = companyRepository.findByBusinessCode(dto.getCompanyBusinessCode())
                .orElseThrow(() -> new RuntimeException("الشركة غير موجودة بالكود: " + dto.getCompanyBusinessCode()));
        // ---------------------------------------------------------

        Account parentAccount = null; // إذا لم يكن محدد المستوي
        int level = 1; // اجعل مستواة رَقَم 1

        // 2. التحقق من الحساب الأبُّ (إن وجد)
        if (dto.getParentAccountId() != null) {
            parentAccount = accountRepository.findById(dto.getParentAccountId())
                    .orElseThrow(() -> new RuntimeException("الحساب الأب غير موجود"));

            // شرط محاسبي: يجب أن يكون الحساب الأبُّ حسابًا رئيسيًا
            if (!Boolean.TRUE.equals(parentAccount.getIsParent())) {
                throw new RuntimeException("لا يمكن إضافة حساب فرعي تحت حساب تحليلي غير رئيسي");
            }

            level = parentAccount.getLevel() + 1;
        }

        // ---  هنا: تمرير ID الشركة الحقيقي المولّد من قاعدة البيانات ---
        // الـ Service لا تزال تحتاج الـ Long ID للتعامل مع قاعدة البيانات في الاستعلامات المعقدة
        // 3. توليد الرمز التلقائي لرمز الحساب (Auto-code Generation)
        String generatedCode = generateNextAccountCode(company.getId(), parentAccount, dto.getAccountType());

        // 4. بناء الـ Entity وحفظه
        Account account = Account.builder()
                .accountCode(generatedCode)
                .nameAr(dto.getNameAr())
                .nameEn(dto.getNameEn())
                .accountType(dto.getAccountType())
                .isParent(dto.getIsParent())
                .level(level)
                .isActive(true)
                .company(company)
                .parentAccount(parentAccount)
                .build();

        Account savedAccount = accountRepository.save(account);
        return mapToResponseDto(savedAccount);
    }

    //========================================================
    // 2. جلب جميع حسابات شركة معينة (قائمة مسطحة)
    //========================================================
    @Override
    @Transactional(readOnly = true)
    // ---  هنا: Long companyId -> String companyBusinessCode ---
    public List<AccountResponseDto> getAccountsByCompany(String companyBusinessCode) {
        // نستخدم حقل الـ BusinessCode الموجود في الـ Account Entity (مفترض وجودة في العِلاقة)
        return accountRepository.findByCompanyBusinessCode(companyBusinessCode)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    //========================================================
    // 3. جلب شجرة الحسابات الكاملة لشركة معينة (هيكل شجري هرمي)
    //========================================================
    @Override
    @Transactional(readOnly = true)
    // ---   هنا: Long companyId -> String companyBusinessCode ---
    public List<AccountTreeDto> getAccountTreeByCompany(String companyBusinessCode) {
        // جلب الحسابات الجذرية (المستوى الأول) وتحويلها بشكل شجري مع أبنائها
        // نحتاج هنا تعديل دالة الـ Repository أيضاً لتستقبل الـ BusinessCode
        List<Account> rootAccounts = accountRepository.findByCompanyBusinessCodeAndParentAccountIsNull(companyBusinessCode);
        return rootAccounts.stream()
                .map(this::mapToTreeDto)
                .collect(Collectors.toList());
    }

    //========================================================
    // 4. جلب حساب محدد بواسطة الـ ID
    //========================================================
    @Override
    @Transactional(readOnly = true)
    public AccountResponseDto getAccountById(Long id) {
        // يبقى كما هو لأنه ID الحساب نفسه
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("الحساب غير موجود بالمعرف: " + id));
        return mapToResponseDto(account);
    }

    //========================================================
    // --- الدالات المساعدة داله توليد رمز الحساب (Helper Methods) ---
    //========================================================
    //   هذا القسم كما هو، لأن الدوال المساعدة تتعامل داخلياً مع الـ Long IDs لسهولة الحسابات،
    // وهذا مقبول تقنيًا داخل طبقة الـ Service ما دام الـ DTO لا يُظهرها.

    private String generateNextAccountCode(Long companyId, Account parentAccount, @NotNull(message = "نوع الحساب مطلوب") AccountType type) {
        if (parentAccount == null) {
            // حساب رئيسي جذري (Root Account)
            String maxCode = accountRepository.findMaxAccountCodeForRootByCompanyId(companyId).orElse(null);
            if (maxCode == null) {
                // أول حساب من هذا النوع
                return String.valueOf(type.ordinal() + 1); // 1 للأصول، 2 للالتزامات... إلخ
            }
            return String.valueOf(Long.parseLong(maxCode) + 1);
        } else {
            // حساب فرعي تابع لأب
            String maxCode = accountRepository.findMaxAccountCodeByCompanyIdAndParentAccountId(companyId, parentAccount.getId()).orElse(null);
            if (maxCode == null) {
                // أول ابن يضاف تحت هذا الأب (مثال: الأب 11 -> الابن الأول 1101)
                return parentAccount.getAccountCode() + "01";
            }
            // إزادة رقم 1 على آخر كود (مثال: 1101 -> 1102)
            long nextCode = Long.parseLong(maxCode) + 1;
            return String.valueOf(nextCode);
        }
    }

    private AccountResponseDto mapToResponseDto(Account account) {
        // نحدث دالة التحويل لتستخدم الـ BusinessCode في الـ Response DTO أيضاً (إذا قمنا بتعديله)
        // إذا لم نعدل AccountResponseDto، ستبقى تستخدم الـ ID داخلياً، لكنها لا تُظهره في JSON.
        return AccountResponseDto.builder()
                .id(account.getId())
                .accountCode(account.getAccountCode())
                .nameAr(account.getNameAr())
                .nameEn(account.getNameEn())
                .accountType(account.getAccountType())
                .isParent(account.getIsParent())
                .level(account.getLevel())
                .isActive(account.getIsActive())
                // --- التعديل هنا: إذا قمنا بتعديل الـ Response DTO ليحتوي على companyBusinessCode ---
                //.companyBusinessCode(account.getCompany().getBusinessCode())
                // .companyId(account.getCompany().getId()) // هذا سيبقى لو لم نعدل DTO
                .parentAccountId(account.getParentAccount() != null ? account.getParentAccount().getId() : null)
                .parentAccountNameAr(account.getParentAccount() != null ? account.getParentAccount().getNameAr() : null)
                .build();
    }

    private AccountTreeDto mapToTreeDto(Account account) {
        // يبقى كما هو لأن Tree DTO لا يعرض الـ Company ID عادة
        AccountTreeDto treeDto = AccountTreeDto.builder()
                .id(account.getId())
                .accountCode(account.getAccountCode())
                .nameAr(account.getNameAr())
                .nameEn(account.getNameEn())
                .accountType(account.getAccountType())
                .isParent(account.getIsParent())
                .level(account.getLevel())
                .isActive(account.getIsActive())
                .children(account.getSubAccounts().stream().map(this::mapToTreeDto).collect(Collectors.toList()))
                .build();

        return treeDto;
    }
}
