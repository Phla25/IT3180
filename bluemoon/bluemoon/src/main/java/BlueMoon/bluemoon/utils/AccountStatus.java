package BlueMoon.bluemoon.utils;

public enum AccountStatus {
    HOAT_DONG("hoat_dong"),
    KHOA("khoa"),
    TAM_NGUNG("tam_ngung");
    
    private final String dbValue;
    AccountStatus(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}