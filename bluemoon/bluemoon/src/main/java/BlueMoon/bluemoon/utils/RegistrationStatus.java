package BlueMoon.bluemoon.utils;

/**
 * Enum thể hiện trạng thái đăng ký dịch vụ.
 * Mapping đúng với cột trang_thai trong bảng dang_ky_dich_vu.
 */
public enum RegistrationStatus {
    cho_duyet("cho_duyet"),
    da_duyet("da_duyet"),
    dang_su_dung("dang_su_dung"),
    da_huy("da_huy"),
    da_ket_thuc("da_ket_thuc");

    private final String description;

    RegistrationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
