package BlueMoon.bluemoon.utils;

public enum InvoiceStatus {
    DA_THANH_TOAN("da_thanh_toan"),
    CHUA_THANH_TOAN("chua_thanh_toan"),
    QUA_HAN("qua_han"),
    GIAM_TRU("giam_tru");
    
    private final String dbValue;
    InvoiceStatus(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}