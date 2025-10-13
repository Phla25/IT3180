package BlueMoon.bluemoon.utils;

public enum NotificationType {
    QUAN_TRONG("quan_trong"),
    BINH_THUONG("binh_thuong"),
    KHAN_CAP("khan_cap");
    
    private final String dbValue;
    NotificationType(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}