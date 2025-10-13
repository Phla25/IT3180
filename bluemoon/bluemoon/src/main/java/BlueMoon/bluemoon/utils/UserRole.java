package BlueMoon.bluemoon.utils;

public enum UserRole {
    BAN_QUAN_TRI("ban_quan_tri"),
    CO_QUAN_CHUC_NANG("co_quan_chuc_nang"),
    KE_TOAN("ke_toan"),
    NGUOI_DUNG_THUONG("nguoi_dung_thuong"),
    KHONG_DUNG_HE_THONG("khong_dung_he_thong");

    private final String dbValue;
    UserRole(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}