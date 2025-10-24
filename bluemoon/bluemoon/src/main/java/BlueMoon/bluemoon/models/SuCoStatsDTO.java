package BlueMoon.bluemoon.models;

public class SuCoStatsDTO {
    private Integer tongSuCo;
    private Double tiLeHoanThanh;
    private Double tiLeDangXuLy;
    public SuCoStatsDTO(Integer tongSuCo, Double tiLeHoanThanh, Double tiLeDangXuLy) {
        this.tongSuCo = tongSuCo;
        this.tiLeHoanThanh = tiLeHoanThanh;
        this.tiLeDangXuLy = tiLeDangXuLy;
    }
    public Integer getTongSuCo() {
        return tongSuCo;
    }
    public void setTongSuCo(Integer tongSuCo) {
        this.tongSuCo = tongSuCo;
    }
    public Double getTiLeHoanThanh() {
        return tiLeHoanThanh;
    }
    public void setTiLeHoanThanh(Double tiLeHoanThanh) {
        this.tiLeHoanThanh = tiLeHoanThanh;
    }
    public Double getTiLeDangXuLy() {
        return tiLeDangXuLy;
    }
    public void setTiLeDangXuLy(Double tiLeDangXuLy) {
        this.tiLeDangXuLy = tiLeDangXuLy;
    }
}
