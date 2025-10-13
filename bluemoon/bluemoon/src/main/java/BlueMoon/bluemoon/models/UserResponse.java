package BlueMoon.bluemoon.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import BlueMoon.bluemoon.entities.DoiTuong;
import BlueMoon.bluemoon.utils.AccountStatus;
import BlueMoon.bluemoon.utils.Gender;
import BlueMoon.bluemoon.utils.ResidentStatus;
import BlueMoon.bluemoon.utils.UserRole;

// DTO dùng để trả về thông tin người dùng cho Client
public class UserResponse {

    private String cccd;
    // KHÔNG BAO GỒM: matKhau (để đảm bảo bảo mật)
    
    private UserRole vaiTro;
    private Boolean laCuDan;
    private String hoVaTen;
    private LocalDate ngaySinh;
    private Gender gioiTinh;
    private String queQuan;
    private String soDienThoai;
    private String email;
    private String ngheNghiep;
    
    private AccountStatus trangThaiTaiKhoan;
    private ResidentStatus trangThaiDanCu;

    // Thông tin quản trị (có thể loại bỏ tùy theo quyền)
    private LocalDateTime ngayTao;
    private LocalDateTime lanDangNhapCuoi;
    private LocalDateTime ngayCapNhat;

    // =======================================================
    // Constructors, Getters, and Setters (Vẫn cần phải thêm vào)
    // =======================================================
    public String getCccd() {
		return cccd;
	}

	public void setCccd(String cccd) {
		this.cccd = cccd;
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

	public UserResponse(DoiTuong doituong) {
    	this.cccd = doituong.getCccd();
    	this.email = doituong.getEmail();
    	this.gioiTinh = doituong.getGioiTinh();
    	this.hoVaTen = doituong.getHoVaTen();
    	this.laCuDan = doituong.getLaCuDan();
    	this.lanDangNhapCuoi = doituong.getLanDangNhapCuoi();
    	this.ngayCapNhat = doituong.getNgayCapNhat();
    	this.ngaySinh = doituong.getNgaySinh();
    	this.ngayTao = doituong.getNgayTao();
    	this.ngheNghiep = doituong.getNgheNghiep();
    	this.queQuan = doituong.getQueQuan();
    	this.soDienThoai = doituong.getSoDienThoai();
    	this.trangThaiDanCu = doituong.getTrangThaiDanCu();
    	this.trangThaiTaiKhoan = doituong.getTrangThaiTaiKhoan();
    	this.vaiTro = doituong.getVaiTro();
    }
	public UserResponse() {};
}