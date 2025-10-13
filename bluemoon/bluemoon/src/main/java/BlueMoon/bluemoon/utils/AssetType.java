package BlueMoon.bluemoon.utils;

public enum AssetType {
    CAN_HO("can_ho"),
    THIET_BI("thiet_bi"),
    TIEN_ICH("tien_ich");
    
    private final String dbValue;
    AssetType(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}