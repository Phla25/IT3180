package BlueMoon.bluemoon.utils;

public enum AssetStatus {
    HOAT_DONG("hoat_dong"),
    BAO_TRI("bao_tri"),
    HONG("hong"),
    NGUNG_HOAT_DONG("ngung_hoat_dong");
    
    private final String dbValue;
    AssetStatus(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}