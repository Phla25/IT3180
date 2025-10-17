package BlueMoon.bluemoon.utils;

public enum IncidentStatus {
    moi_tiep_nhan("moi_tiep_nhan"),
    dang_xu_ly("dang_xu_ly"),
    cho_phe_duyet("cho_phe_duyet"),
    da_hoan_thanh("da_hoan_thanh"),
    da_huy("da_huy");
    
    private final String dbValue;
    IncidentStatus(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}