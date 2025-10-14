package BlueMoon.bluemoon.utils;

public enum PriorityLevel {
    THAP("thap"),
    BINH_THUONG("binh_thuong"),
    CAO("cao"),
    KHAN_CAP("khan_cap");
    
    private final String dbValue;
    PriorityLevel(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}