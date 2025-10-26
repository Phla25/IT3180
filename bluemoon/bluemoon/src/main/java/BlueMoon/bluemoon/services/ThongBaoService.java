package BlueMoon.bluemoon.services;

import BlueMoon.bluemoon.daos.ThongBaoDAO;
import BlueMoon.bluemoon.daos.PhanHoiThongBaoDAO;
import BlueMoon.bluemoon.entities.ThongBao;
import BlueMoon.bluemoon.entities.DoiTuong;
import BlueMoon.bluemoon.entities.PhanHoiThongBao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ThongBaoService {

	@Autowired
	private ThongBaoDAO thongBaoDAO;

	@Autowired
	private PhanHoiThongBaoDAO phanHoiThongBaoDAO;

	// Giả định có một Service khác để lấy thông tin người dùng/Admin
	// @Autowired
	// private DoiTuongService doiTuongService;

	// ---------------------- Logic dành cho ADMIN (Gửi thông báo)
	// ----------------------

	/**
	 * Tạo và gửi một thông báo mới.
	 * 
	 * @param tieuDe   Tiêu đề thông báo.
	 * @param noiDung  Nội dung thông báo.
	 * @param nguoiTao Đối tượng Admin/BQT tạo ra thông báo.
	 * @return Thông báo đã được lưu (sent)
	 */
	@Transactional
	public ThongBao taoVaGuiThongBao(String tieuDe, String noiDung, DoiTuong nguoiTao) {

		// 1. Khởi tạo đối tượng ThongBao
		ThongBao thongBao = new ThongBao();
		thongBao.setTieuDe(tieuDe);
		thongBao.setNoiDung(noiDung);
		thongBao.setNguoiGui(nguoiTao);
		thongBao.setThoiGianGui(LocalDateTime.now()); // Set thời gian gửi

		// 2. Lưu vào CSDL (gửi thông báo)
		return thongBaoDAO.save(thongBao);
	}

	// ---------------------- Logic dành cho CƯ DÂN (Nhận thông báo)
	// ----------------------

	/**
	 * Lấy danh sách tất cả thông báo (mới nhất trước).
	 * 
	 * @return Danh sách thông báo.
	 */
	public List<ThongBao> layTatCaThongBaoMoiNhat() {
		// Cư dân nhận tất cả thông báo chung.
		return thongBaoDAO.findAllByOrderByThoiGianGuiDesc();
	}

	/**
	 * Lấy chi tiết thông báo theo mã.
	 * 
	 * @param maThongBao Mã thông báo.
	 * @return Thông báo tương ứng.
	 * @throws ResourceNotFoundException Nếu không tìm thấy thông báo.
	 */
	// Trong ThongBaoService.java

	// Thay đổi kiểu trả về
	public Optional<ThongBao> layThongBaoTheoMa(Integer maThongBao) {
		// Trả về Optional trực tiếp (không còn lỗi kiểu)
		return thongBaoDAO.findById(maThongBao);
	}

	/**
	 * Cư dân gửi phản hồi cho một thông báo.
	 * 
	 * @param maThongBao Mã thông báo được phản hồi.
	 * @param nguoiGui   Đối tượng cư dân gửi phản hồi.
	 * @param noiDung    Nội dung phản hồi.
	 * @return Phản hồi đã được lưu.
	 */
	@Transactional
	public PhanHoiThongBao themPhanHoi(Integer maThongBao, DoiTuong nguoiGui, String noiDung) {
		// 1. Tìm thông báo
		ThongBao thongBao = layThongBaoTheoMa(maThongBao);

		// 2. Tạo đối tượng phản hồi
		PhanHoiThongBao phanHoi = new PhanHoiThongBao();
		phanHoi.setThongBao(thongBao);
		phanHoi.setNguoiGui(nguoiGui);
		phanHoi.setNoiDung(noiDung);
		// thoiGianGui được tự động set bởi @PrePersist trong Entity

		// 3. Lưu phản hồi
		return phanHoiThongBaoDAO.save(phanHoi);
	}

	/**
	 * Lấy tất cả phản hồi cho một thông báo. (Dùng cho Admin hoặc Cư dân xem luồng
	 * phản hồi)
	 * 
	 * @param maThongBao Mã thông báo.
	 * @return Danh sách phản hồi, sắp xếp theo thứ tự thời gian.
	 */
	public List<PhanHoiThongBao> layPhanHoiTheoThongBao(Integer maThongBao) {
		return phanHoiThongBaoDAO.findByThongBaoMaThongBaoOrderByThoiGianGuiAsc(maThongBao);
	}
}