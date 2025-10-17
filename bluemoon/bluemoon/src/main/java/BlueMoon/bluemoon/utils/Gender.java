package BlueMoon.bluemoon.utils;

public enum Gender {
    nam("nam"),
    nu("nu");
    
    private final String dbValue;
    Gender(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}