package BlueMoon.bluemoon.utils;

public enum ResidentStatus {
    ROI_DI("roi_di"),
    O_CHUNG_CU("o_chung_cu"),
    DA_CHET("da_chet");
    
    private final String dbValue;
    ResidentStatus(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}
