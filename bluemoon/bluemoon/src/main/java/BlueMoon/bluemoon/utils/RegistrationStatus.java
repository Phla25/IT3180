package BlueMoon.bluemoon.utils;

/**
 * Enum thể hiện trạng thái đăng ký dịch vụ.
 * Mapping đúng với cột trang_thai trong bảng dang_ky_dich_vu.
 */
public enum RegistrationStatus {
    CHO_DUYET("cho_duyet"),
    DA_DUYET("da_duyet"),
    DANG_SU_DUNG("dang_su_dung"),
    DA_HUY("da_huy"),
    DA_KET_THUC("da_ket_thuc");

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
