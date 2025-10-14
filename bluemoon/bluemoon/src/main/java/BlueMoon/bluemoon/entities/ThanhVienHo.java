package BlueMoon.bluemoon.entities;

import java.time.LocalDate;

import BlueMoon.bluemoon.utils.TerminationReason;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

/**
 * Bảng thanh_vien_ho: mô tả việc một người (cccd) thuộc vào hộ nào, 
 * từ ngày nào, với quan hệ ra sao, và có phải là chủ hộ không.
 */
@Entity
@Table(name = "thanh_vien_ho")
public class ThanhVienHo {

    // Khóa chính tổng hợp
    @SuppressWarnings("rawtypes")
	@EmbeddedId
    private ThanhVienHoID id;

    // Ánh xạ phần cccd từ khóa tổng hợp sang đối tượng DoiTuong
    @MapsId("cccd")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cccd", referencedColumnName = "cccd", nullable = false)
    private DoiTuong doiTuong;

    // Hộ gia đình mà người này thuộc về
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_ho", nullable = false)
    private HoGiaDinh hoGiaDinh;

    @Column(name = "la_chu_ho")
    private Boolean laChuHo = false;

    @Column(name = "quan_he_voi_chu_ho", length = 50)
    private String quanHeVoiChuHo;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @Enumerated(EnumType.STRING)
    @Column(name = "ly_do_ket_thuc", length = 100)
    private TerminationReason lyDoKetThuc;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    // ===== Constructors =====
    public ThanhVienHo() {}
    
    @SuppressWarnings("rawtypes")
	public ThanhVienHo(ThanhVienHoID id, DoiTuong doiTuong, HoGiaDinh hoGiaDinh,
                       Boolean laChuHo, String quanHeVoiChuHo) {
        this.id = id;
        this.doiTuong = doiTuong;
        this.hoGiaDinh = hoGiaDinh;
        this.laChuHo = laChuHo;
        this.quanHeVoiChuHo = quanHeVoiChuHo;
    }

    // ===== Getters & Setters =====
    @SuppressWarnings("rawtypes")
	public ThanhVienHoID getId() { return id; }
    @SuppressWarnings("rawtypes")
	public void setId(ThanhVienHoID id) { this.id = id; }

    public DoiTuong getDoiTuong() { return doiTuong; }
    public void setDoiTuong(DoiTuong doiTuong) { this.doiTuong = doiTuong; }

    public HoGiaDinh getHoGiaDinh() { return hoGiaDinh; }
    public void setHoGiaDinh(HoGiaDinh hoGiaDinh) { this.hoGiaDinh = hoGiaDinh; }

    public Boolean getLaChuHo() { return laChuHo; }
    public void setLaChuHo(Boolean laChuHo) { this.laChuHo = laChuHo; }

    public String getQuanHeVoiChuHo() { return quanHeVoiChuHo; }
    public void setQuanHeVoiChuHo(String quanHeVoiChuHo) { this.quanHeVoiChuHo = quanHeVoiChuHo; }

    public LocalDate getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(LocalDate ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }

    public TerminationReason getLyDoKetThuc() { return lyDoKetThuc; }
    public void setLyDoKetThuc(TerminationReason lyDoKetThuc) { this.lyDoKetThuc = lyDoKetThuc; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
