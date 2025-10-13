package BlueMoon.bluemoon.utils;

public enum TerminationReason {
    CHUYEN_DI("chuyen_di"),
    QUA_DOI("qua_doi"),
    CHUYEN_CHU_HO("chuyen_chu_ho"),
    TACH_HO("tach_ho");
    
    private final String dbValue;
    TerminationReason(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}