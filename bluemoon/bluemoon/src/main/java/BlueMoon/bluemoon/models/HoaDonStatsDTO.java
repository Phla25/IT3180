package BlueMoon.bluemoon.models;

import java.math.BigDecimal;

/**
 * DTO chứa thống kê hóa đơn cho Dashboard Cư Dân và Kế Toán.
 */
public class HoaDonStatsDTO {
    
    // =======================================================
    // Dữ liệu cơ bản (Dùng chung cho Cư Dân & Kế Toán)
    // =======================================================
    // Số tiền chưa thanh toán của hộ (Cho Cư Dân) hoặc Tổng số tiền chưa thu (Cho Kế Toán)
    public BigDecimal tongChuaThanhToan = BigDecimal.ZERO; 
    public String trangThai = "Tất cả đã thanh toán";
    public Integer tongHoaDonChuaThanhToan = 0; // Số hóa đơn chưa thanh toán/chưa thu
    
    // =======================================================
    // Dữ liệu mở rộng cho KẾ TOÁN (FINANCIAL FOCUS)
    // =======================================================
    public BigDecimal tongThuThangNay = BigDecimal.ZERO;
    public BigDecimal tongQuaHan = BigDecimal.ZERO;
    public Integer soHoaDonQuaHan = 0;
    public Double phanTramTangTruong = 0.0; // Biến động so với tháng trước (%)

    // LOẠI BỎ: public BigDecimal tongChiThangNay = BigDecimal.ZERO;
    // LOẠI BỎ: public BigDecimal loiNhuanRong = BigDecimal.ZERO;
    

    // === Constructors ===
    public HoaDonStatsDTO() {}
    public HoaDonStatsDTO(BigDecimal tongChuaThanhToan, String trangThai, Integer tongSoHoaDonChuaThanhToan){
        this.tongChuaThanhToan = tongChuaThanhToan;
        this.trangThai = trangThai;
        this.tongHoaDonChuaThanhToan = tongSoHoaDonChuaThanhToan;   
    }
    // Nếu bạn muốn giữ lại constructor có tham số cho trường hợp cần thiết, hãy thêm vào đây.
    // Ví dụ: public HoaDonStatsDTO(BigDecimal soTien, String trangThai, Integer soLuong) { ... }
    
    // === Getters & Setters ===
    // (Thêm vào đây nếu bạn cần truy cập private fields, nhưng hiện tại đã là public)

}