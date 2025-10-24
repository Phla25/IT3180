package BlueMoon.bluemoon.models;

// Ví dụ: Class này được truyền vào model với tên "dichVuStats"
public class DichVuStatsDTO {
    // Đảm bảo thuộc tính này tồn tại và có getter/là public
    private Integer tongDichVu; 
    // THUỘC TÍNH MỚI CẦN THÊM
    private String trangThai; 
    // END THUỘC TÍNH MỚI

    public Integer getTongDichVu() {
        return tongDichVu;
    }
    // ... các getter/setter khác
    public void setTongDichVu(Integer tongDichVu) {
        this.tongDichVu = tongDichVu;
    }
    public DichVuStatsDTO() {
    }
    public DichVuStatsDTO(Integer tongDichVu) {
        this.tongDichVu = tongDichVu;
    }
    public DichVuStatsDTO(Integer soDichVu, Integer tongDichVu) {
        this.tongDichVu = tongDichVu;
    }
    public Integer getSoDichVu() {
        return tongDichVu;
    }
    public void setSoDichVu(Integer soDichVu) {
        this.tongDichVu = soDichVu;
    }
    public Integer getTongSoDichVu() {
        return tongDichVu;
    }
    public void setTongSoDichVu(Integer tongSoDichVu) {
        this.tongDichVu = tongSoDichVu;
    }

    // GETTER VÀ SETTER MỚI CHO TRANGTHAI
    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
    // HẾT GETTER VÀ SETTER MỚI
}