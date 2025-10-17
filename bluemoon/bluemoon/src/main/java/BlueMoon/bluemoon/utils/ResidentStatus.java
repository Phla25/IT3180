package BlueMoon.bluemoon.utils;

public enum ResidentStatus {
    roi_di("roi_di"),
    o_chung_cu("o_chung_cu"),
    da_chet("da_chet");
    
    private final String dbValue;
    ResidentStatus(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}
