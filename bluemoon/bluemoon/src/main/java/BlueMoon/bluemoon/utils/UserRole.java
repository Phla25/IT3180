package BlueMoon.bluemoon.utils;

public enum UserRole {
    ban_quan_tri("ban_quan_tri"),
    co_quan_chuc_nang("co_quan_chuc_nang"),
    ke_toan("ke_toan"),
    nguoi_dung_thuong("nguoi_dung_thuong"),
    khong_dung_he_thong("khong_dung_he_thong");

    private final String dbValue;
    UserRole(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}