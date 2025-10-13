package BlueMoon.bluemoon.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType; // Import quan trọng
import jakarta.persistence.Enumerated; // Import quan trọng
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

// Import các Enums từ package utils
import BlueMoon.bluemoon.utils.UserRole;
import BlueMoon.bluemoon.utils.Gender;
import BlueMoon.bluemoon.utils.AccountStatus;
import BlueMoon.bluemoon.utils.ResidentStatus;


@Entity
@Table(name = "doi_tuong")
public class DoiTuong {

    // Khóa chính (Primary Key)
    @Id
    @Column(name = "cccd", length = 12)
    private String cccd;

    // Các cột thông tin tài khoản
    @Column(name = "mat_khau", nullable = false, length = 255)
    private String matKhau;

    // SỬ DỤNG ENUM
    @Enumerated(EnumType.STRING)
    @Column(name = "vai_tro", nullable = false, length = 20)
    private UserRole vaiTro; // Đã thay thế String bằng UserRole

    @Column(name = "la_cu_dan", nullable = false)
    private Boolean laCuDan;

    // Các cột thông tin cá nhân
    @Column(name = "ho_va_ten", nullable = false, length = 100)
    private String hoVaTen;

    @Column(name = "ngay_sinh", nullable = false)
    private LocalDate ngaySinh;

    // SỬ DỤNG ENUM
    @Enumerated(EnumType.STRING)
    @Column(name = "gioi_tinh", length = 10)
    private Gender gioiTinh; // Đã thay thế String bằng Gender

    @Column(name = "que_quan", columnDefinition = "TEXT")
    private String queQuan;

    @Column(name = "so_dien_thoai", length = 15)
    private String soDienThoai;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "nghe_nghiep", length = 100)
    private String ngheNghiep;

    // Trạng thái tài khoản và dân cư
    // SỬ DỤNG ENUM
    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_tai_khoan", length = 20)
    private AccountStatus trangThaiTaiKhoan; // Đã thay thế String bằng AccountStatus

    // SỬ DỤNG ENUM
    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_dan_cu", length = 20)
    private ResidentStatus trangThaiDanCu; // Đã thay thế String bằng ResidentStatus

    // Các cột theo dõi thời gian
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "lan_dang_nhap_cuoi")
    private LocalDateTime lanDangNhapCuoi;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    // =======================================================
    // JPA Callbacks (Tự động cập nhật thời gian)
    // =======================================================

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
        this.ngayCapNhat = LocalDateTime.now();
        
        // Thiết lập giá trị mặc định cho trạng thái tài khoản nếu chưa được gán
        if (this.trangThaiTaiKhoan == null) {
            this.trangThaiTaiKhoan = AccountStatus.HOAT_DONG; 
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.ngayCapNhat = LocalDateTime.now();
    }

	public String getCccd() {
		return cccd;
	}

	public void setCccd(String cccd) {
		this.cccd = cccd;
	}

	public String getMatKhau() {
		return matKhau;
	}

	public void setMatKhau(String matKhau) {
		this.matKhau = matKhau;
	}

	public UserRole getVaiTro() {
		return vaiTro;
	}

	public void setVaiTro(UserRole vaiTro) {
		this.vaiTro = vaiTro;
	}

	public Boolean getLaCuDan() {
		return laCuDan;
	}

	public void setLaCuDan(Boolean laCuDan) {
		this.laCuDan = laCuDan;
	}

	public String getHoVaTen() {
		return hoVaTen;
	}

	public void setHoVaTen(String hoVaTen) {
		this.hoVaTen = hoVaTen;
	}

	public LocalDate getNgaySinh() {
		return ngaySinh;
	}

	public void setNgaySinh(LocalDate ngaySinh) {
		this.ngaySinh = ngaySinh;
	}

	public Gender getGioiTinh() {
		return gioiTinh;
	}

	public void setGioiTinh(Gender gioiTinh) {
		this.gioiTinh = gioiTinh;
	}

	public String getQueQuan() {
		return queQuan;
	}

	public void setQueQuan(String queQuan) {
		this.queQuan = queQuan;
	}

	public String getSoDienThoai() {
		return soDienThoai;
	}

	public void setSoDienThoai(String soDienThoai) {
		this.soDienThoai = soDienThoai;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNgheNghiep() {
		return ngheNghiep;
	}

	public void setNgheNghiep(String ngheNghiep) {
		this.ngheNghiep = ngheNghiep;
	}

	public AccountStatus getTrangThaiTaiKhoan() {
		return trangThaiTaiKhoan;
	}

	public void setTrangThaiTaiKhoan(AccountStatus trangThaiTaiKhoan) {
		this.trangThaiTaiKhoan = trangThaiTaiKhoan;
	}

	public ResidentStatus getTrangThaiDanCu() {
		return trangThaiDanCu;
	}

	public void setTrangThaiDanCu(ResidentStatus trangThaiDanCu) {
		this.trangThaiDanCu = trangThaiDanCu;
	}

	public LocalDateTime getNgayTao() {
		return ngayTao;
	}

	public void setNgayTao(LocalDateTime ngayTao) {
		this.ngayTao = ngayTao;
	}

	public LocalDateTime getLanDangNhapCuoi() {
		return lanDangNhapCuoi;
	}

	public void setLanDangNhapCuoi(LocalDateTime lanDangNhapCuoi) {
		this.lanDangNhapCuoi = lanDangNhapCuoi;
	}

	public LocalDateTime getNgayCapNhat() {
		return ngayCapNhat;
	}

	public void setNgayCapNhat(LocalDateTime ngayCapNhat) {
		this.ngayCapNhat = ngayCapNhat;
	}

	public DoiTuong(String cccd, String matKhau, UserRole vaiTro, Boolean laCuDan, String hoVaTen, LocalDate ngaySinh,
			Gender gioiTinh, String queQuan, String soDienThoai, String email, String ngheNghiep,
			AccountStatus trangThaiTaiKhoan, ResidentStatus trangThaiDanCu, LocalDateTime ngayTao,
			LocalDateTime lanDangNhapCuoi, LocalDateTime ngayCapNhat) {
		super();
		this.cccd = cccd;
		this.matKhau = matKhau;
		this.vaiTro = vaiTro;
		this.laCuDan = laCuDan;
		this.hoVaTen = hoVaTen;
		this.ngaySinh = ngaySinh;
		this.gioiTinh = gioiTinh;
		this.queQuan = queQuan;
		this.soDienThoai = soDienThoai;
		this.email = email;
		this.ngheNghiep = ngheNghiep;
		this.trangThaiTaiKhoan = trangThaiTaiKhoan;
		this.trangThaiDanCu = trangThaiDanCu;
		this.ngayTao = ngayTao;
		this.lanDangNhapCuoi = lanDangNhapCuoi;
		this.ngayCapNhat = ngayCapNhat;
	}

	public DoiTuong() {};
    // =======================================================
    // Constructors, Getters, and Setters (Vẫn cần phải thêm vào)
    // =======================================================
    
}