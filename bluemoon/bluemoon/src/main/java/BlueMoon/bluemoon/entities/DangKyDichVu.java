package BlueMoon.bluemoon.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import BlueMoon.bluemoon.utils.RegistrationStatus;
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

@Entity
@DynamicUpdate
@Table(name = "dang_ky_dich_vu")
public class DangKyDichVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_dang_ky")
    private Integer maDangKy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cccd_nguoi_dung", nullable = false)
    @JsonIgnore
    private DoiTuong nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_dich_vu", nullable = false)
    @JsonIgnore
    private DichVu dichVu;

    @Column(name = "mo_ta_yeu_cau", columnDefinition = "TEXT")
    private String moTaYeuCau;

    @Column(name = "ngay_dang_ky")
    private LocalDateTime ngayDangKy;

    @Column(name = "ngay_bat_dau_su_dung")
    private LocalDate ngayBatDauSuDung;

    @Column(name = "ngay_ket_thuc_su_dung")
    private LocalDate ngayKetThucSuDung;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 20)
    private RegistrationStatus trangThai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cccd_nguoi_duyet")
    @JsonIgnore
    private DoiTuong nguoiDuyet;

    @Column(name = "ngay_duyet")
    private LocalDateTime ngayDuyet;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @PrePersist
    protected void onCreate() {
        if (ngayDangKy == null) {
            ngayDangKy = LocalDateTime.now();
        }
    }

	public Integer getMaDangKy() {
		return maDangKy;
	}

	public void setMaDangKy(Integer maDangKy) {
		this.maDangKy = maDangKy;
	}

	public DoiTuong getNguoiDung() {
		return nguoiDung;
	}

	public void setNguoiDung(DoiTuong nguoiDung) {
		this.nguoiDung = nguoiDung;
	}

	public DichVu getDichVu() {
		return dichVu;
	}

	public void setDichVu(DichVu dichVu) {
		this.dichVu = dichVu;
	}

	public String getMoTaYeuCau() {
		return moTaYeuCau;
	}

	public void setMoTaYeuCau(String moTaYeuCau) {
		this.moTaYeuCau = moTaYeuCau;
	}

	public LocalDateTime getNgayDangKy() {
		return ngayDangKy;
	}

	public void setNgayDangKy(LocalDateTime ngayDangKy) {
		this.ngayDangKy = ngayDangKy;
	}

	public LocalDate getNgayBatDauSuDung() {
		return ngayBatDauSuDung;
	}

	public void setNgayBatDauSuDung(LocalDate ngayBatDauSuDung) {
		this.ngayBatDauSuDung = ngayBatDauSuDung;
	}

	public LocalDate getNgayKetThucSuDung() {
		return ngayKetThucSuDung;
	}

	public void setNgayKetThucSuDung(LocalDate ngayKetThucSuDung) {
		this.ngayKetThucSuDung = ngayKetThucSuDung;
	}

	public RegistrationStatus getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(RegistrationStatus trangThai) {
		this.trangThai = trangThai;
	}

	public DoiTuong getNguoiDuyet() {
		return nguoiDuyet;
	}

	public void setNguoiDuyet(DoiTuong nguoiDuyet) {
		this.nguoiDuyet = nguoiDuyet;
	}

	public LocalDateTime getNgayDuyet() {
		return ngayDuyet;
	}

	public void setNgayDuyet(LocalDateTime ngayDuyet) {
		this.ngayDuyet = ngayDuyet;
	}

	public String getGhiChu() {
		return ghiChu;
	}

	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}

	public DangKyDichVu(Integer maDangKy, DoiTuong nguoiDung, DichVu dichVu, String moTaYeuCau,
			LocalDateTime ngayDangKy, LocalDate ngayBatDauSuDung, LocalDate ngayKetThucSuDung,
			RegistrationStatus trangThai, DoiTuong nguoiDuyet, LocalDateTime ngayDuyet, String ghiChu) {
		super();
		this.maDangKy = maDangKy;
		this.nguoiDung = nguoiDung;
		this.dichVu = dichVu;
		this.moTaYeuCau = moTaYeuCau;
		this.ngayDangKy = ngayDangKy;
		this.ngayBatDauSuDung = ngayBatDauSuDung;
		this.ngayKetThucSuDung = ngayKetThucSuDung;
		this.trangThai = trangThai;
		this.nguoiDuyet = nguoiDuyet;
		this.ngayDuyet = ngayDuyet;
		this.ghiChu = ghiChu;
	}

	public DangKyDichVu() {
		super();
	}
    
}
