package BlueMoon.bluemoon.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicUpdate;

import BlueMoon.bluemoon.utils.AssetStatus;
import BlueMoon.bluemoon.utils.ServiceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Bảng dịch_vụ: mô tả các loại dịch vụ do Ban quản trị cung cấp
 * (VD: gửi xe, dọn vệ sinh, nước sinh hoạt, điện, wifi, v.v.)
 */
@Entity
@Table(name = "dich_vu")
@DynamicUpdate
public class DichVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_dich_vu")
    private Integer maDichVu;

    @Column(name = "ten_dich_vu", nullable = false, length = 100)
    private String tenDichVu;

    // Người tạo (Ban quản trị phụ trách dịch vụ này)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cccd_ban_quan_tri", referencedColumnName = "cccd")
    private DoiTuong banQuanTri;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "gia_thanh", nullable = false, precision = 15, scale = 2)
    private BigDecimal giaThanh;

    @Column(name = "don_vi", length = 20)
    private String donVi;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_dich_vu", length = 30, nullable = false)
    private ServiceType loaiDichVu;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 20)
    private AssetStatus trangThai = AssetStatus.hoat_dong; // mặc định là hoạt động

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    // ====== Constructors ======
    public DichVu() {}

    public DichVu(String tenDichVu, DoiTuong banQuanTri, String moTa,
                  BigDecimal giaThanh, String donVi, ServiceType loaiDichVu) {
        this.tenDichVu = tenDichVu;
        this.banQuanTri = banQuanTri;
        this.moTa = moTa;
        this.giaThanh = giaThanh;
        this.donVi = donVi;
        this.loaiDichVu = loaiDichVu;
    }

    // ====== Lifecycle ======
    @PrePersist
    public void onCreate() {
        this.ngayTao = LocalDateTime.now();
    }

    // ====== Getters & Setters ======
    public Integer getMaDichVu() { return maDichVu; }
    public void setMaDichVu(Integer maDichVu) { this.maDichVu = maDichVu; }

    public String getTenDichVu() { return tenDichVu; }
    public void setTenDichVu(String tenDichVu) { this.tenDichVu = tenDichVu; }

    public DoiTuong getBanQuanTri() { return banQuanTri; }
    public void setBanQuanTri(DoiTuong banQuanTri) { this.banQuanTri = banQuanTri; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public BigDecimal getGiaThanh() { return giaThanh; }
    public void setGiaThanh(BigDecimal giaThanh) { this.giaThanh = giaThanh; }

    public String getDonVi() { return donVi; }
    public void setDonVi(String donVi) { this.donVi = donVi; }

    public ServiceType getLoaiDichVu() { return loaiDichVu; }
    public void setLoaiDichVu(ServiceType loaiDichVu) { this.loaiDichVu = loaiDichVu; }

    public AssetStatus getTrangThai() { return trangThai; }
    public void setTrangThai(AssetStatus trangThai) { this.trangThai = trangThai; }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
}
