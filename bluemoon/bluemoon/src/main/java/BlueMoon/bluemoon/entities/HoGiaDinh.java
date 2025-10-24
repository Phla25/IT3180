package BlueMoon.bluemoon.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;

import BlueMoon.bluemoon.utils.HouseholdStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "ho_gia_dinh")
@DynamicUpdate
public class HoGiaDinh {

    @Id
    @Column(name = "ma_ho", length = 20)
    private String maHo;

    @Column(name = "ten_ho", nullable = false, length = 100)
    private String tenHo;

    @Column(name = "ngay_thanh_lap")
    private LocalDate ngayThanhLap;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 20)
    private HouseholdStatus trangThai;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    // 🔹 Liên kết với Thành Viên Hộ (Đã thêm)
    @OneToMany(mappedBy = "hoGiaDinh", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ThanhVienHo> thanhVienHoList = new ArrayList<>();

    // 🔹 Một hộ có thể sở hữu nhiều tài sản (căn hộ, chỗ đậu xe, kho,...)
    @OneToMany(mappedBy = "hoGiaDinh", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaiSanChungCu> taiSanList = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.ngayCapNhat = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.ngayCapNhat = LocalDateTime.now();
    }

    // ==== Getters & Setters ====
    public String getMaHo() { return maHo; }
    public void setMaHo(String maHo) { this.maHo = maHo; }

    public String getTenHo() { return tenHo; }
    public void setTenHo(String tenHo) { this.tenHo = tenHo; }

    public LocalDate getNgayThanhLap() { return ngayThanhLap; }
    public void setNgayThanhLap(LocalDate ngayThanhLap) { this.ngayThanhLap = ngayThanhLap; }

    public HouseholdStatus getTrangThai() { return trangThai; }
    public void setTrangThai(HouseholdStatus trangThai) { this.trangThai = trangThai; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public LocalDateTime getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(LocalDateTime ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }

    public List<TaiSanChungCu> getTaiSanList() { return taiSanList; }
    public void setTaiSanList(List<TaiSanChungCu> taiSanList) { this.taiSanList = taiSanList; }
    
    // 🔹 Getters/Setters cho ThanhVienHoList
    public List<ThanhVienHo> getThanhVienHoList() {
        return thanhVienHoList;
    }

    public void setThanhVienHoList(List<ThanhVienHo> thanhVienHoList) {
        this.thanhVienHoList = thanhVienHoList;
    }
    
    /**
     * Helper: Trả về DoiTuong là Chủ Hộ hiện tại.
     * Sử dụng để truy cập Chủ Hộ an toàn qua Entity ThanhVienHo.
     */
    public DoiTuong getChuHo() {
        if (thanhVienHoList != null) {
            return thanhVienHoList.stream()
                    // Lọc ra thành viên là Chủ Hộ
                    .filter(tvh -> Boolean.TRUE.equals(tvh.getLaChuHo()))
                    // Lấy đối tượng DoiTuong
                    .map(tvh -> tvh.getDoiTuong())
                    .findFirst()
                    .orElse(null); // Trả về null nếu không tìm thấy
        }
        return null;
    }
}