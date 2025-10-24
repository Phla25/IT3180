package BlueMoon.bluemoon.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import BlueMoon.bluemoon.entities.DoiTuong;
import BlueMoon.bluemoon.entities.HoaDon;
import BlueMoon.bluemoon.models.HoaDonStatsDTO;
import BlueMoon.bluemoon.services.HoaDonService;
import BlueMoon.bluemoon.services.NguoiDungService;
import BlueMoon.bluemoon.utils.InvoiceStatus;

@Controller
@RequestMapping("/accountant")
public class AccountantController {

    @Autowired
    private NguoiDungService nguoiDungService;

    @Autowired
    private HoaDonService hoaDonService;

    /**
     * Helper: Lấy đối tượng DoiTuong hiện tại (Kế Toán)
     */
    private DoiTuong getCurrentUser(Authentication auth) {
        String id = auth.getName(); // Lấy CCCD/ID từ principal
        // SỬ DỤNG SERVICE ĐÃ CÓ
        Optional<DoiTuong> userOpt = nguoiDungService.timKeToanTheoID(id);
        return userOpt.orElse(null); 
    }

    // =======================================================
    // DASHBOARD (Giữ nguyên)
    // =======================================================
    
    @GetMapping("/dashboard")
    public String showAccountantDashboard(Model model, Authentication auth) {
        
        DoiTuong user = getCurrentUser(auth);
        if (user == null) {
            return "redirect:/login?error=notfound";
        }
        
        // 1. Thông tin người dùng (cho header và sidebar)
        model.addAttribute("user", user);

        // 2. Lấy số liệu thống kê tài chính
        HoaDonStatsDTO stats = hoaDonService.getAccountantStats();
        
        // 3. Truyền các thông số riêng lẻ vào Model 
        model.addAttribute("tongThuThangNay", stats.tongThuThangNay);
        model.addAttribute("tongChuaThu", stats.tongChuaThanhToan);
        model.addAttribute("soHoaDonChuaThu", (long) stats.tongHoaDonChuaThanhToan);
        model.addAttribute("tongQuaHan", stats.tongQuaHan);
        model.addAttribute("soHoaDonQuaHan", (long) stats.soHoaDonQuaHan);
        
        // 4. Lấy danh sách hóa đơn cần xử lý
        List<HoaDon> hoaDonCanXacNhan = hoaDonService.getHoaDonCanXacNhan(InvoiceStatus.chua_thanh_toan, 5); 
        model.addAttribute("hoaDonCanXacNhan", hoaDonCanXacNhan);

        return "dashboard-accountant"; 
    }
    
    // =======================================================
    // PROFILE
    // =======================================================
    @GetMapping("/profile")
    public String showAccountantProfile(Model model, Authentication auth) {
        DoiTuong user = getCurrentUser(auth);
        if (user == null) {
            return "redirect:/login?error=notfound";
        }
        model.addAttribute("user", user);
        return "profile-accountant"; // Tên file Thymeleaf đã tạo
    }
    // =======================================================
    // PROFILE EDIT / CHANGE PASSWORD
    // =======================================================
    // Hiển thị form Đổi Mật Khẩu
    @GetMapping("/change-password")
    public String showAccountantChangePasswordForm(Model model, Authentication auth) {
        DoiTuong user = getCurrentUser(auth); 
        if (user == null) {
            return "redirect:/login?error=auth";
        }
        model.addAttribute("user", user);
        return "change-password-accountant"; 
    }

    // Xử lý POST request Đổi Mật Khẩu
    @PostMapping("/change-password")
    public String handleAccountantChangePassword(@RequestParam("matKhauCu") String matKhauCu,
                                            @RequestParam("matKhauMoi") String matKhauMoi,
                                            @RequestParam("xacNhanMatKhau") String xacNhanMatKhau,
                                            Authentication auth,
                                            RedirectAttributes redirectAttributes) {
        
        DoiTuong currentUser = getCurrentUser(auth); 
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi xác thực người dùng.");
            return "redirect:/accountant/profile";
        }

        if (!matKhauMoi.equals(xacNhanMatKhau)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu mới và xác nhận mật khẩu không khớp.");
            return "redirect:/accountant/change-password";
        }
        
        try {
            nguoiDungService.doiMatKhau(currentUser.getCccd(), matKhauCu, matKhauMoi);
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
            return "redirect:/logout"; 
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/accountant/change-password";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            return "redirect:/accountant/change-password";
        }
    }

    // Hiển thị form Cập Nhật Thông Tin Cá Nhân
    @GetMapping("/profile/edit")
    public String showAccountantEditProfileForm(Model model, Authentication auth) {
        DoiTuong user = getCurrentUser(auth); 
        if (user == null) {
            return "redirect:/login?error=auth";
        }
        model.addAttribute("user", user); 
        // Cần import Gender
        // model.addAttribute("genders", BlueMoon.bluemoon.utils.Gender.values());
        return "edit-profile-accountant"; 
    }

    // Xử lý POST request Cập Nhật Thông Tin Cá Nhân
    @PostMapping("/profile/edit")
    public String handleAccountantEditProfile(@ModelAttribute("user") DoiTuong doiTuongCapNhat,
                                        Authentication auth,
                                        RedirectAttributes redirectAttributes) {
        
        DoiTuong currentUser = getCurrentUser(auth); 
        if (currentUser == null || !currentUser.getCccd().equals(doiTuongCapNhat.getCccd())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi xác thực người dùng.");
            return "redirect:/accountant/profile";
        }
        
        try {
            nguoiDungService.capNhatThongTinNguoiDung(doiTuongCapNhat);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin cá nhân thành công!");
            return "redirect:/accountant/profile";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/accountant/profile/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống khi cập nhật: " + e.getMessage());
            return "redirect:/accountant/profile/edit";
        }
    }    
}