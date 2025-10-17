package BlueMoon.bluemoon.utils;

public enum InvoiceStatus {
    da_thanh_toan("da_thanh_toan"),
    chua_thanh_toan("chua_thanh_toan"),
    qua_han("qua_han"),
    giam_tru("giam_tru");
    
    private final String dbValue;
    InvoiceStatus(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}