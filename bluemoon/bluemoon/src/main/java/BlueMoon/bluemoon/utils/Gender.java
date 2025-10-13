package BlueMoon.bluemoon.utils;

public enum Gender {
    NAM("nam"),
    NU("nu");
    
    private final String dbValue;
    Gender(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}