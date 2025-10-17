package BlueMoon.bluemoon.utils;

public enum PriorityLevel {
    thap("thap"),
    binh_thuong("binh_thuong"),
    cao("cao"),
    khan_cap("khan_cap");
    
    private final String dbValue;
    PriorityLevel(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}