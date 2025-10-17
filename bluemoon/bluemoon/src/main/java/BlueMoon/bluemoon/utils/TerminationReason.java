package BlueMoon.bluemoon.utils;

public enum TerminationReason {
    chuyen_di("chuyen_di"),
    qua_doi("qua_doi"),
    chuyen_chu_ho("chuyen_chu_ho"),
    tach_ho("tach_ho");
    
    private final String dbValue;
    TerminationReason(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}