package com.aseel.accounting.user.model;

public enum Role {
    ADMIN_SYSTEM,        // مدير النظام (صلاحيات كامله علي النظام الي واجه financial_setup.html )
    ROLE_ADMIN,         // مدير الشركه (كل الصلاحيات في الشركه المدير لها يتم نقله مباشرة الي واجه الحسابات accounts_management.html)
    ROLE_ACCOUNTANT,    // محاسب (قيود، تقارير يتم نقله الي واجه القيود والتقاريرfinancial_operations.html  )
    ROLE_CASHIER        // موظف مخازن مبيعات ومشتريات (المخزن فواتير بيع وشراء يتم نقله الي واجه المخزن والفواتير  inventory_setup.html)
}
