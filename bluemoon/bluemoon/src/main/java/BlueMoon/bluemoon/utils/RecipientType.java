package BlueMoon.bluemoon.utils;

public enum RecipientType {
    TAT_CA("tat_ca"),
    CHU_HO("chu_ho"),
    THEO_HO("theo_ho");
    
    private final String dbValue;
    RecipientType(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}