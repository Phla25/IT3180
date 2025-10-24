package BlueMoon.bluemoon.models;

public class ThongBaoStatsDTO {
    private Integer soLuongMoi;
    private String trangThai;
    public ThongBaoStatsDTO(Integer soLuongMoi, String trangThai) {
        this.soLuongMoi = soLuongMoi;
        this.trangThai = trangThai;
    }
    public Integer getSoLuongMoi() {
        return soLuongMoi;
    }
    public void setSoLuongMoi(Integer soLuongMoi) {
        this.soLuongMoi = soLuongMoi;
    }
    public String getTrangThai() {
        return trangThai;
    }
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
