package BlueMoon.bluemoon.utils;

public enum IncidentStatus {
    MOI_TIEP_NHAN("moi_tiep_nhan"),
    DANG_XU_LY("dang_xu_ly"),
    CHO_PHE_DUYET("cho_phe_duyet"),
    DA_HOAN_THANH("da_hoan_thanh"),
    DA_HUY("da_huy");
    
    private final String dbValue;
    IncidentStatus(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}