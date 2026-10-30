package com.aseel.accounting.user.service;

import com.aseel.accounting.company.model.Company;
import com.aseel.accounting.company.repository.CompanyRepository;
import com.aseel.accounting.user.dto.AuthResponseDTO;
import com.aseel.accounting.user.dto.LoginRequestDTO;
import com.aseel.accounting.user.dto.RegisterRequestDTO;
import com.aseel.accounting.user.dto.UserResponseDTO;
import com.aseel.accounting.user.model.Role;
import com.aseel.accounting.user.model.User;
import com.aseel.accounting.user.repository.UserRepository;
import com.aseel.accounting.user.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository; // مستودع الشركات للتحقق من حالتها
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    // 📌 كونستركتور واحد موحد لحقن التبعيات (Dependency Injection) بشكل صحيح
    public UserService(UserRepository userRepository,
                       CompanyRepository companyRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    // =========================================================================
    // 1. تسجيل مستخدم جديد (مع التحقق من الصلاحيات والشركات)
    // =========================================================================
    @Transactional
    public void registerUser(RegisterRequestDTO dto, String currentUsername) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("اسم المستخدم مستخدم مسبقاً!");
        }

        // أ. شرط عدم السماح بإنشاء أكثر من مدير عام (ADMIN_SYSTEM) واحد في النظام
        if (dto.getRole() == Role.ADMIN_SYSTEM) {
            boolean adminExists = userRepository.findAll().stream()
                    .anyMatch(u -> u.getRole() == Role.ADMIN_SYSTEM);
            if (adminExists) {
                throw new RuntimeException("عذراً، يوجد مدير عام للنظام بالفعل ولا يمكن إنشاء مدير آخر!");
            }
        }

        // ب. توليد businessCode تلقائياً بشكل إلزامي للمستخدم الجديد
        String businessCode = "USR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // ج. تحديد كود الشركة بذكاء بناءً على صلاحية الشخص الذي يقوم بالإضافة
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("المستخدم الحالي غير موجود"));

        String targetCompanyCode;
        if (currentUser.getRole() == Role.ADMIN_SYSTEM) {
            // المدير العام يختار الشركة المحددة في الـ DTO
            targetCompanyCode = dto.getCompanyBusinessCode();
            if (targetCompanyCode == null || targetCompanyCode.trim().isEmpty()) {
                throw new RuntimeException("يجب اختيار الشركة المرتبطة بالمستخدم");
            }
        } else if (currentUser.getRole() == Role.ROLE_ADMIN) {
            // مدير الشركة: يتم فرض شركته تلقائياً بغض النظر عما أُرسل من الواجهة
            targetCompanyCode = currentUser.getCompanyBusinessCode();
        } else {
            throw new AccessDeniedException("ليس لديك صلاحية لإنشاء مستخدمين جدد");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .fullName(dto.getFullName())
                .role(dto.getRole())
                .businessCode(businessCode)
                .companyBusinessCode(targetCompanyCode)
                .isActive(true)
                .build();

        userRepository.save(user);
    }

    // =========================================================================
    // 2. تسجيل الدخول (مع التحقق من حالة المستخدم وحالة الشركة المرتبطة)
    // =========================================================================
    public AuthResponseDTO login(LoginRequestDTO dto) {
        // أ. المصادقة عبر اسم المستخدم وكلمة المرور
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        // ب. جلب بيانات المستخدم من قاعدة البيانات
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));

        // ج. التحقق هل حساب المستخدم الشخصي مفعل؟
        if (!user.isActive()) {
            throw new RuntimeException("عذراً، هذا الحساب معطل. يرجى مراجعة الإدارة.");
        }

        // د. التحقق من حالة الشركة المرتبطة (إذا لم يكن مدير عام للنظام)
        if (user.getRole() != Role.ADMIN_SYSTEM) {
            if (user.getCompanyBusinessCode() == null || user.getCompanyBusinessCode().trim().isEmpty()) {
                throw new RuntimeException("المستخدم غير مرتبط بأي شركة!");
            }

            // البحث عن الشركة بكود الـ String والتحقق من نشاطها
            Company company = companyRepository.findByBusinessCode(user.getCompanyBusinessCode())
                    .orElseThrow(() -> new RuntimeException("الشركة المرتبطة بالمستخدم غير موجودة"));

            if (!company.isActive()) {
                throw new RuntimeException("عذراً، هذه الشركة معطلة حالياً من قِبل إدارة النظام.");
            }
        }

        // هـ. توليد الـ Token
        String token = tokenProvider.generateToken(principal);

        // و. إرجاع الاستجابة المتكاملة (شاملة كود الشركة النصي)
        return new AuthResponseDTO(
                token,
                user.getUsername(),
                user.getFullName(),
                user.getRole().name(),
                user.getCompanyBusinessCode()
        );
    }

    // =========================================================================
    // 3. عرض المستخدمين بناءً على الصلاحيات
    // =========================================================================
    public List<UserResponseDTO> getUsersByCurrentAdmin(String currentUsername) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));

        List<User> users;
        if (currentUser.getRole() == Role.ADMIN_SYSTEM) {
            users = userRepository.findAll();
        } else if (currentUser.getRole() == Role.ROLE_ADMIN) {
            users = userRepository.findByCompanyBusinessCode(currentUser.getCompanyBusinessCode());
        } else {
            throw new AccessDeniedException("ليس لديك صلاحية لعرض المستخدمين");
        }

        return users.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // =========================================================================
    // 4. تحديث بيانات المستخدم
    // =========================================================================
    @Transactional
    public void updateUser(String businessCode, RegisterRequestDTO dto, String currentUsername) {
        User targetUser = userRepository.findByBusinessCode(businessCode)
                .orElseThrow(() -> new RuntimeException("المستخدم المراد تعديله غير موجود"));

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));

        // مدير الشركة لا يمكنه التعديل إلا على مستخدمي شركته فقط
        if (currentUser.getRole() == Role.ROLE_ADMIN &&
                !targetUser.getCompanyBusinessCode().equals(currentUser.getCompanyBusinessCode())) {
            throw new AccessDeniedException("لا يمكنك تعديل مستخدم يتبع لشركة أخرى");
        }

        // منع تعديل أو ترقية أي مستخدم ليكون مدير عام إذا كان هناك مدير عام مسبقاً
        if (dto.getRole() == Role.ADMIN_SYSTEM && targetUser.getRole() != Role.ADMIN_SYSTEM) {
            boolean adminExists = userRepository.findAll().stream()
                    .anyMatch(u -> u.getRole() == Role.ADMIN_SYSTEM);
            if (adminExists) {
                throw new RuntimeException("يوجد مدير عام للنظام بالفعل ولا يمكن تعيين مدير آخر!");
            }
        }

        targetUser.setFullName(dto.getFullName());
        targetUser.setRole(dto.getRole());

        // تحديث كلمة المرور إذا تم إرسالها غير فارغة
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            targetUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // المدير العام فقط يمكنه تغيير شركة المستخدم
        if (currentUser.getRole() == Role.ADMIN_SYSTEM && dto.getCompanyBusinessCode() != null && !dto.getCompanyBusinessCode().trim().isEmpty()) {
            targetUser.setCompanyBusinessCode(dto.getCompanyBusinessCode());
        }

        userRepository.save(targetUser);
    }

    // =========================================================================
    // 5. إيقاف أو تفعيل المستخدم (Toggle Status)
    // =========================================================================
    @Transactional
    public void toggleUserStatus(String businessCode, String currentUsername) {
        User targetUser = userRepository.findByBusinessCode(businessCode)
                .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));

        if (currentUser.getRole() == Role.ROLE_ADMIN &&
                !targetUser.getCompanyBusinessCode().equals(currentUser.getCompanyBusinessCode())) {
            throw new AccessDeniedException("لا صلاحية لك على مستخدمي هذه الشركة");
        }

        targetUser.setActive(!targetUser.isActive());
        userRepository.save(targetUser);
    }

    // =========================================================================
    // 6. حذف المستخدم
    // =========================================================================
    @Transactional
    public void deleteUser(String businessCode, String currentUsername) {
        User targetUser = userRepository.findByBusinessCode(businessCode)
                .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));

        if (currentUser.getRole() == Role.ROLE_ADMIN &&
                !targetUser.getCompanyBusinessCode().equals(currentUser.getCompanyBusinessCode())) {
            throw new AccessDeniedException("لا صلاحية لك بحذف مستخدم خارج شركتك");
        }

        userRepository.delete(targetUser);
    }

    // =========================================================================
    // 🛠️ ميثود مساعدة لتحويل الـ Entity إلى DTO
    // =========================================================================
    private UserResponseDTO mapToDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .businessCode(user.getBusinessCode())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole())
                .companyBusinessCode(user.getCompanyBusinessCode())
                .isActive(user.isActive())
                .build();
    }
}
