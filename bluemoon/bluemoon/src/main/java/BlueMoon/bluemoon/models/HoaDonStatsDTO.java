package BlueMoon.bluemoon.models;

import java.math.BigDecimal;

/**
 * DTO chứa thống kê hóa đơn cho Dashboard Cư Dân.
 */
public class HoaDonStatsDTO {
    public BigDecimal soTienChuaThanhToan = BigDecimal.ZERO;
    public String trangThai = "Tất cả đã thanh toán";
    public Integer tongHoaDonChuaThanhToan = 0;

    // Constructors và Getters/Setters...
}