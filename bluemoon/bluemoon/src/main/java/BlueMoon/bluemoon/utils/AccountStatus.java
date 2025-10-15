package BlueMoon.bluemoon.utils;

public enum AccountStatus {
    HOAT_DONG("hoat_dong"),
    KHOA("khoa"),
    TAM_NGUNG("tam_ngung"),
    CHUA_KICH_HOAT("chua_kich_hoat");
    
    private final String dbValue;
    AccountStatus(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}