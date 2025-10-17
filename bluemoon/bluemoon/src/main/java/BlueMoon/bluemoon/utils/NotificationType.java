package BlueMoon.bluemoon.utils;

public enum NotificationType {
    quan_trong("quan_trong"),
    binh_thuong("binh_thuong"),
    khan_cap("khan_cap");
    
    private final String dbValue;
    NotificationType(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}