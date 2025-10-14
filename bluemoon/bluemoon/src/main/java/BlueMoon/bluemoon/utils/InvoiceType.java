package BlueMoon.bluemoon.utils;

public enum InvoiceType {
    DICH_VU("dich_vu"),
    SUA_CHUA("sua_chua"),
    PHAT("phat"),
    KHAC("khac");
    
    private final String dbValue;
    InvoiceType(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}