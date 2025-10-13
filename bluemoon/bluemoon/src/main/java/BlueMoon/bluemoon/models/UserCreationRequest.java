package BlueMoon.bluemoon.models;

import java.time.LocalDate;
import BlueMoon.bluemoon.utils.UserRole;
import BlueMoon.bluemoon.utils.Gender;

// DTO dùng để tạo mới một tài khoản người dùng
public class UserCreationRequest {

    // Khóa chính và thông tin bắt buộc
    private String cccd;
    private String matKhau; // Sẽ được hash ở tầng Service
    private UserRole vaiTro;
    private Boolean laCuDan;

    // Thông tin cá nhân
    private String hoVaTen;
    private LocalDate ngaySinh;
    private Gender gioiTinh;
    private String queQuan;
    private String soDienThoai;
    private String email;
    private String ngheNghiep;
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
	public UserCreationRequest(String cccd, String matKhau, UserRole vaiTro, Boolean laCuDan, String hoVaTen,
			LocalDate ngaySinh, Gender gioiTinh, String queQuan, String soDienThoai, String email, String ngheNghiep) {
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
	}
	public UserCreationRequest() {
		super();
	}
    // KHÔNG BAO GỒM: ngayTao, ngayCapNhat, trangThai (sẽ được Service thiết lập)

    // =======================================================
    // Constructors, Getters, and Setters (Vẫn cần phải thêm vào)
    // =======================================================
    
}