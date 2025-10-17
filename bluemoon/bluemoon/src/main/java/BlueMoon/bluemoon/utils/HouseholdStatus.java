package BlueMoon.bluemoon.utils;

public enum HouseholdStatus {
    hoat_dong("hoat_dong"),
    da_chuyen_di("da_chuyen_di"),
    giai_the("giai_the");
    
    private final String dbValue;
    HouseholdStatus(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}