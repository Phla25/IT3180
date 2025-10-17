package BlueMoon.bluemoon.utils;

public enum InvoiceType {
    dich_vu("dich_vu"),
    sua_chua("sua_chua"),
    phat("phat"),
    khac("khac");
    
    private final String dbValue;
    InvoiceType(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}