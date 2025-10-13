package BlueMoon.bluemoon.utils;

public enum ServiceType {
    DINH_KY("dinh_ky"),
    THEO_YEU_CAU("theo_yeu_cau");
    
    private final String dbValue;
    ServiceType(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
}