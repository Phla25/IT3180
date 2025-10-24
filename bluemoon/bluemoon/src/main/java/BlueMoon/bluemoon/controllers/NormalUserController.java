package BlueMoon.bluemoon.controllers;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import BlueMoon.bluemoon.entities.DoiTuong;
import BlueMoon.bluemoon.entities.HoGiaDinh;
import BlueMoon.bluemoon.models.DichVuStatsDTO;
import BlueMoon.bluemoon.models.HoGiaDinhDTO;
import BlueMoon.bluemoon.models.HoaDonStatsDTO;
import BlueMoon.bluemoon.models.SuCoStatsDTO;
import BlueMoon.bluemoon.models.ThongBaoStatsDTO;
import BlueMoon.bluemoon.services.HoaDonService;
import BlueMoon.bluemoon.services.NguoiDungService;
import BlueMoon.bluemoon.services.ThanhVienHoService;

@Controller
public class NormalUserController {

    @Autowired
    private NguoiDungService nguoiDungService;

    @Autowired
    private ThanhVienHoService thanhVienHoService;
    
    @Autowired private HoaDonService hoaDonService;

    /**
     * Helper: Lấy đối tượng DoiTuong hiện tại
     * Giả sử username của principal là CCCD (đã được cấu hình trong UserDetailsService)
     */
    private DoiTuong getCurrentUser(Authentication auth) {
        String cccd = auth.getName(); // Lấy CCCD từ principal/username
        Optional<DoiTuong> userOpt = nguoiDungService.timNguoiDungThuongTheoCCCD(cccd);
        return userOpt.orElse(null); 
    }

    @GetMapping("/resident/dashboard")
    public String residentDashboard(Model model, Authentication auth) {
        DoiTuong currentUser = getCurrentUser(auth);
        if (currentUser == null) {
            return "redirect:/login?error=notfound";
        }
        model.addAttribute("user", currentUser);

        // B1: Lấy thông tin Căn hộ/Hộ gia đình
        HoGiaDinhDTO canHoInfo = thanhVienHoService.getCanHoInfo(currentUser.getCccd(), currentUser.getHoVaTen());
        model.addAttribute("canHoInfo", canHoInfo);

        // B2: Lấy đối tượng HoGiaDinh (CẦN LOGIC THỰC TẾ TRONG TVH SERVICE)
        Optional<HoGiaDinh> hoGiaDinhOpt = thanhVienHoService.getHoGiaDinhByCccd(currentUser.getCccd()); 
        HoGiaDinh hoGiaDinh = hoGiaDinhOpt.orElse(null);
    
        // B3: Lấy Dữ liệu Hóa Đơn
        if (hoGiaDinh != null) {
            model.addAttribute("hoaDonStats", hoaDonService.getHoaDonStats(hoGiaDinh));
            model.addAttribute("recentHoaDon", hoaDonService.getRecentHoaDon(hoGiaDinh, 3));
        } else {
            // Tránh lỗi khi HoGiaDinh null
            model.addAttribute("hoaDonStats", new HoaDonStatsDTO()); 
            model.addAttribute("recentHoaDon", Collections.emptyList());
        }
    
        // B4: MOCK Dữ liệu còn lại (SỬA DỤNG DTO CÓ THUỘC TÍNH)
        
        // Mock DichVuStatsDTO
        // Mock DichVuStatsDTO (Bổ sung thiết lập trạng thái mặc định)
        DichVuStatsDTO dichVuStats = new DichVuStatsDTO();
        dichVuStats.setTongDichVu(0); // Đảm bảo số lượng dịch vụ là 0 khi mock
        dichVuStats.setTrangThai("Chưa đăng ký dịch vụ"); // Đặt giá trị cho trangThai
        model.addAttribute("dichVuStats", dichVuStats);
        
        // Mock SuCoStatsDTO (Sử dụng constructor có tham số)
        model.addAttribute("suCoStats", new SuCoStatsDTO(0, 0.0, 0.0));
        
        // Mock ThongBaoStatsDTO (Sử dụng constructor có tham số)
        model.addAttribute("thongBaoStats", new ThongBaoStatsDTO(0, "Không có thông báo mới")); 
        
        // Mock HoGiaDinhStatsDTO (Giả định đã tạo HoGiaDinhStatsDTO với constructor mặc định)
        model.addAttribute("hoGiaDinhStats", new HoGiaDinhDTO());

        return "dashboard-resident";
    }

    @GetMapping("/resident/profile")
    public String showResidentProfile(Model model, Authentication auth) {
        
        DoiTuong currentUser = getCurrentUser(auth); 
        
        if (currentUser == null) {
            // Trường hợp lỗi (ví dụ: Session hết hạn hoặc không tìm thấy user)
            return "redirect:/login?error=auth";
        }

        // 1. Thêm đối tượng user vào Model để hiển thị trong Thymeleaf
        model.addAttribute("user", currentUser);

        // 2. Trả về tên file Thymeleaf
        // Sử dụng tên mới để tránh nhầm lẫn với dashboard: profile-resident-detail.html
        return "profile-resident"; 
    }
    // NEW: Hiển thị form Cập Nhật Thông Tin Cá Nhân
    @GetMapping("/resident/profile/edit")
    public String showEditProfileForm(Model model, Authentication auth) {
        DoiTuong currentUser = getCurrentUser(auth); 
        if (currentUser == null) {
            return "redirect:/login?error=auth";
        }
        model.addAttribute("user", currentUser); 
        return "edit-profile-resident";
    }
    @PostMapping("/resident/profile/edit")
    public String handleEditProfile(@ModelAttribute("user") DoiTuong doiTuongCapNhat,
                                    Authentication auth,
                                    RedirectAttributes redirectAttributes) {
        
        DoiTuong currentUser = getCurrentUser(auth); 
        if (currentUser == null || !currentUser.getCccd().equals(doiTuongCapNhat.getCccd())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi xác thực người dùng hoặc thông tin CCCD không khớp.");
            return "redirect:/resident/profile";
        }
        
        try {
            // Gọi Service để xử lý logic cập nhật thông tin
            nguoiDungService.capNhatThongTinNguoiDung(doiTuongCapNhat);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin cá nhân thành công!");
            return "redirect:/resident/profile";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/resident/profile/edit"; // Quay lại form chỉnh sửa
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống khi cập nhật: " + e.getMessage());
            return "redirect:/resident/profile/edit";
        }
    }
    // Hiến thị trang đổi mật khẩu
    @GetMapping("/resident/change-password")
    public String showChangePasswordForm(Model model, Authentication auth) {
        DoiTuong currentUser = getCurrentUser(auth); 
        if (currentUser == null) {
            return "redirect:/login?error=auth";
        }
        model.addAttribute("user", currentUser); 
        return "change-password-resident";
    }
    // Ghi nhận, cập nhật đổi mật khẩu
    @PostMapping("/resident/change-password")
    public String handleChangePassword(@RequestParam("matKhauCu") String matKhauCu,
                                     @RequestParam("matKhauMoi") String matKhauMoi,
                                     @RequestParam("xacNhanMatKhau") String xacNhanMatKhau,
                                     Authentication auth,
                                     RedirectAttributes redirectAttributes) {
        
        DoiTuong currentUser = getCurrentUser(auth); 
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi xác thực người dùng.");
            return "redirect:/resident/profile";
        }

        if (!matKhauMoi.equals(xacNhanMatKhau)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu mới và xác nhận mật khẩu không khớp.");
            return "redirect:/resident/change-password";
        }
        
        try {
            // Gọi Service để xử lý logic đổi mật khẩu
            nguoiDungService.doiMatKhau(currentUser.getCccd(), matKhauCu, matKhauMoi);
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công! Vui lòng đăng nhập lại với mật khẩu mới.");
            // Chuyển hướng về trang đăng nhập sau khi đổi thành công để buộc người dùng đăng nhập lại
            return "redirect:/logout"; 
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/resident/change-password";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            return "redirect:/resident/change-password";
        }
    }
}