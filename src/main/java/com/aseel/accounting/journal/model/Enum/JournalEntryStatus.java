package com.aseel.accounting.journal.model.Enum;

public enum JournalEntryStatus {
    DRAFT,     // مسودة (يمكن تعديله أو حذفه، ولا يؤثر على الأرصدة)
    POSTED,    // مرحّل (غير قابل للتعديل أو الحذف، ويؤثر على الأرصدة)
    CANCELLED  // ملغى
}
