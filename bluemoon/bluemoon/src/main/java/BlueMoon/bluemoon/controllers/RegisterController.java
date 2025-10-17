package BlueMoon.bluemoon.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import BlueMoon.bluemoon.entities.DoiTuong;
import BlueMoon.bluemoon.services.NguoiDungService;
import BlueMoon.bluemoon.utils.UserRole; // Giả định enum UserRole đã được định nghĩa

@Controller
public class RegisterController {
    
    @Autowired
    private NguoiDungService nguoiDungService; 

    /**
     * Hiển thị trang đăng ký tài khoản (GET /register)
     */
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register"; // Trả về tên file HTML: register.html
    }

    /**
     * Xử lý dữ liệu đăng ký tài khoản (POST /register)
     */
    @PostMapping("/register")
    public String registerNewUser(
        @RequestParam("hoTen") String hoTen,
        @RequestParam("cccd") String cccd, // Sẽ là username
        @RequestParam("sdt") String sdt,
        @RequestParam("email") String email,
        @RequestParam("diaChi") String diaChi,
        @RequestParam("matKhau") String matKhau,
        @RequestParam("xacNhanMatKhau") String xacNhanMatKhau,
        RedirectAttributes redirectAttributes) 
    {
        // 1. Kiểm tra xác nhận mật khẩu
        if (!matKhau.equals(xacNhanMatKhau)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp.");
            return "redirect:/register";
        }

        // 2. Tạo đối tượng người dùng (chỉ các trường bắt buộc)
        DoiTuong doiTuong = new DoiTuong();
        doiTuong.setHoVaTen(hoTen);
        doiTuong.setCccd(cccd);
        doiTuong.setSoDienThoai(sdt);
        doiTuong.setEmail(email);
        doiTuong.setQueQuan(diaChi);
        doiTuong.setLaCuDan(true); // Đặt mặc định là cư dân
        doiTuong.setVaiTro(UserRole.nguoi_dung_thuong); // Đặt vai trò mặc định
        // Các trường khác (tuổi, giới tính, chủ hộ, v.v.) có thể được cập nhật sau

        try {
            // 3. Gọi Service để mã hóa và lưu người dùng
            // (Bạn sẽ cần thêm phương thức này vào NguoiDungService)
            // nguoiDungService.dangKyNguoiDung(doiTuong, matKhau); 
            nguoiDungService.dangKyNguoiDung(doiTuong, matKhau);
            redirectAttributes.addFlashAttribute("message", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login"; 
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi Đăng ký: " + e.getMessage());
            return "redirect:/register";
        }
    }
}