package BlueMoon.bluemoon.utils;

public enum AssetStatus {
    da_duoc_thue("da_duoc_thue"),
    hoat_dong("hoat_dong"),
    bao_tri("bao_tri"),
    hong("hong"),
    ngùn_hoat_dong("ngung_hoat_dong");
    
    private final String dbValue;
    AssetStatus(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}