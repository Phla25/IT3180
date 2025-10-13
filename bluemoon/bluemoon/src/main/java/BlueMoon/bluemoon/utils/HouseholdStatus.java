package BlueMoon.bluemoon.utils;

public enum HouseholdStatus {
    HOAT_DONG("hoat_dong"),
    DA_CHUYEN_DI("da_chuyen_di"),
    GIAI_THE("giai_the");
    
    private final String dbValue;
    HouseholdStatus(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}