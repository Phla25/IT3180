package BlueMoon.bluemoon.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "anh_bao_cao")
public class AnhBaoCao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_anh")
    private Integer maAnh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_bao_cao", nullable = false)
    private BaoCaoSuCo baoCaoSuCo; // Liên kết với sự cố

    @Column(name = "duong_dan_anh", nullable = false, columnDefinition = "TEXT")
    private String duongDanAnh; // Path hoặc URL tới ảnh

    @Column(name = "ngay_them")
    private LocalDateTime ngayThem;

    @Column(name = "mo_ta", length = 255)
    private String moTa;

    // ================= Constructors =================

    public AnhBaoCao() {
    }

    public AnhBaoCao(BaoCaoSuCo baoCaoSuCo, String duongDanAnh, String moTa) {
        this.baoCaoSuCo = baoCaoSuCo;
        this.duongDanAnh = duongDanAnh;
        this.moTa = moTa;
        this.ngayThem = LocalDateTime.now();
    }

    // ================= Lifecycle Callback =================

    @PrePersist
    protected void onCreate() {
        if (this.ngayThem == null) {
            this.ngayThem = LocalDateTime.now();
        }
    }

    // ================= Getters and Setters =================

    public Integer getMaAnh() {
        return maAnh;
    }

    public void setMaAnh(Integer maAnh) {
        this.maAnh = maAnh;
    }

    public BaoCaoSuCo getBaoCaoSuCo() {
        return baoCaoSuCo;
    }

    public void setBaoCaoSuCo(BaoCaoSuCo baoCaoSuCo) {
        this.baoCaoSuCo = baoCaoSuCo;
    }

    public String getDuongDanAnh() {
        return duongDanAnh;
    }

    public void setDuongDanAnh(String duongDanAnh) {
        this.duongDanAnh = duongDanAnh;
    }

    public LocalDateTime getNgayThem() {
        return ngayThem;
    }

    public void setNgayThem(LocalDateTime ngayThem) {
        this.ngayThem = ngayThem;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    // ================= toString =================

    @Override
    public String toString() {
        return "AnhBaoCao{" +
                "maAnh=" + maAnh +
                ", duongDanAnh='" + duongDanAnh + '\'' +
                ", ngayThem=" + ngayThem +
                ", moTa='" + moTa + '\'' +
                '}';
    }
}
