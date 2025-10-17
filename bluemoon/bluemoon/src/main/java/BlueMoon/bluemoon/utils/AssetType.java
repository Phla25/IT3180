package BlueMoon.bluemoon.utils;

public enum AssetType {
    can_ho("can_ho"),
    thiet_bi("thiet_bi"),
    tien_ich("tien_ich");
    
    private final String dbValue;
    AssetType(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}