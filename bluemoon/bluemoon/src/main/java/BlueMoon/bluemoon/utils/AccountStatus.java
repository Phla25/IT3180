package BlueMoon.bluemoon.utils;

public enum AccountStatus {
    hoat_dong("hoat_dong"),
    khoa("khoa"),
    tam_ngung("tam_ngung"),
    chua_kich_hoat("chua_kich_hoat");
    
    private final String dbValue;
    AccountStatus(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}