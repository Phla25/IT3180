package BlueMoon.bluemoon.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import BlueMoon.bluemoon.daos.DoiTuongDAO;
import BlueMoon.bluemoon.entities.DoiTuong;
import BlueMoon.bluemoon.utils.AccountStatus;
import BlueMoon.bluemoon.utils.ResidentStatus;
import jakarta.transaction.Transactional;

@Service
public class NguoiDungService {
    @Autowired
    private DoiTuongDAO doiTuongDAO;

    // Thêm vào NguoiDungService.java

    @Transactional
    public DoiTuong dangKyNguoiDung(DoiTuong doiTuong, String matKhau) {
        // 1. Kiểm tra CCCD/Email đã tồn tại chưa (quan trọng)
        if (doiTuongDAO.timNguoiDungThuongTheoCCCD(doiTuong.getCccd()).isPresent()) {
            throw new IllegalArgumentException("Người dùng đã tồn tại.");
        }
        if (doiTuongDAO.findByEmail(doiTuong.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại.");
        }

        // 2. Mã hóa mật khẩu
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(matKhau);
        // 3. Lưu thông tin mật khẩu đã mã hóa
        doiTuong.setMatKhau(hashedPassword);
        doiTuong.setTrangThaiTaiKhoan(AccountStatus.hoat_dong);
        doiTuong.setLaCuDan(true);
        doiTuong.setVaiTro(BlueMoon.bluemoon.utils.UserRole.nguoi_dung_thuong);
        doiTuong.setResetToken(null);
        doiTuong.setResetTokenExpiry(null);
        doiTuong.setTrangThaiDanCu(ResidentStatus.o_chung_cu); 
        doiTuong.setNgayTao(LocalDateTime.now());
        doiTuong.setNgayCapNhat(LocalDateTime.now());
        doiTuong.setNgaySinh(doiTuongDAO.timCuDanBangCccd(doiTuong.getCccd()).getNgaySinh());
        // 4. Lưu đối tượng người dùng vào DB
        return doiTuongDAO.save(doiTuong);
    }

    /**
     * Đổi mật khẩu
     */
    public void doiMatKhau(String cccd, String matKhauCu, String matKhauMoi) {
        DoiTuong doiTuong = doiTuongDAO.findByCccd(cccd)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(matKhauCu, doiTuong.getMatKhau())) {
            throw new IllegalArgumentException("Mật khẩu cũ không chính xác");
        }

        // Mã hóa và lưu mật khẩu mới
        String hashedPassword = passwordEncoder.encode(matKhauMoi);
        doiTuong.setMatKhau(hashedPassword);
        doiTuongDAO.save(doiTuong);
    }

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Tạo yêu cầu reset mật khẩu
     */
    @Transactional
    public void taoYeuCauResetMatKhau(String email) {
        DoiTuong doiTuong = doiTuongDAO.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại trong hệ thống"));

        // Tạo token ngẫu nhiên
        String token = UUID.randomUUID().toString();
    
        // THAY ĐỔI: Dùng 10 phút (0.1666 giờ) cho tính an toàn cao hơn và khớp với nội dung email.
        // CŨ: LocalDateTime expiry = LocalDateTime.now().plusHours(2/15); // = 0 giờ, sai cú pháp.
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10); 
    
        // Lưu token và thời gian hết hạn
        doiTuong.setResetToken(token);
        doiTuong.setResetTokenExpiry(expiry);
        doiTuongDAO.save(doiTuong);

        // Gửi email chứa link reset mật khẩu
        String resetLink = "http://localhost:8080/reset-password?token=" + token;
        guiEmailResetMatKhau(doiTuong.getEmail(), resetLink);
    }

    /**
     * Reset mật khẩu bằng token
     */
    @Transactional
    public void resetMatKhau(String token, String matKhauMoi) {
        DoiTuong doiTuong = doiTuongDAO.findByResetToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Token không hợp lệ"));

        // Kiểm tra token còn hiệu lực
        if (doiTuong.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token đã hết hạn");
        }

        // Cập nhật mật khẩu mới
        // SỬA LỖI CÚ PHÁP: Gọi đúng hàm helper
        String hashedPassword = BCryptPasswordEncoderHelper(matKhauMoi);
        doiTuong.setMatKhau(hashedPassword);
    
        // Xóa token
        doiTuong.setResetToken(null);
        doiTuong.setResetTokenExpiry(null);
        
        doiTuongDAO.save(doiTuong);
    }

    // Đổi tên hàm helper để tránh nhầm lẫn với constructor của BCryptPasswordEncoder
    private String BCryptPasswordEncoderHelper(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    private void guiEmailResetMatKhau(String email, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Yêu cầu đặt lại mật khẩu");
        message.setText("Để đặt lại mật khẩu, vui lòng click vào link sau:\n" + resetLink + 
                        "\nLink có hiệu lực trong 10 phút.");
        mailSender.send(message);
    }
}