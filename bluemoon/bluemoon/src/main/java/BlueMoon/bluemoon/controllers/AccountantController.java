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
import BlueMoon.bluemoon.services.HoGiaDinhService;
import BlueMoon.bluemoon.services.HoaDonService;
import BlueMoon.bluemoon.services.NguoiDungService;
import BlueMoon.bluemoon.utils.InvoiceType;

@Controller
@RequestMapping("/accountant")
public class AccountantController {

    @Autowired
    private NguoiDungService nguoiDungService;

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired private HoGiaDinhService hoGiaDinhService;

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
        List<HoaDon> hoaDonChoXacNhan = hoaDonService.getHoaDonChoXacNhan(5); 
        model.addAttribute("hoaDonCanXacNhan", hoaDonChoXacNhan); // Giữ tên biến để không sửa Thymeleaf quá nhiều

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
    // =======================================================
    // QUẢN LÝ HÓA ĐƠN (CRUD)
    // =======================================================

    /**
     * Hiển thị danh sách tất cả hóa đơn.
     * URL: /accountant/fees
     */
    @GetMapping("/fees")
    public String showAccountantFees(Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));
        List<HoaDon> hoaDonList = hoaDonService.getAllHoaDon(); 
        model.addAttribute("hoaDonList", hoaDonList);
        return "fees-accountant"; // Tên file Thymeleaf mới
    }
    
    /**
     * Hiển thị form Tạo/Chỉnh sửa Hóa đơn (GET).
     * URL: /accountant/fee-form?id={maHoaDon} (Edit) hoặc /accountant/fee-form (Add)
     */
    @GetMapping("/fee-form")
    public String showFeeForm(@RequestParam(value = "id", required = false) Integer maHoaDon, 
                              Model model, 
                              Authentication auth) {
        
        model.addAttribute("user", getCurrentUser(auth));
        HoaDon hoaDon = (maHoaDon != null) ? 
                        hoaDonService.getHoaDonById(maHoaDon).orElse(new HoaDon()) : 
                        new HoaDon();
        
        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("pageTitle", (maHoaDon != null) ? "Chỉnh Sửa Hóa Đơn #" + maHoaDon : "Tạo Hóa Đơn Mới");
        model.addAttribute("invoiceTypes", InvoiceType.values()); 
        // Lấy danh sách Hộ gia đình cho Select box

        model.addAttribute("allHo", hoGiaDinhService.getAllHouseholds()); // Cần Autowired HoGiaDinhService
        
        return "invoice-add-edit-accountant"; 
    }
    
    /**
     * Xử lý Tạo/Chỉnh sửa Hóa đơn (POST).
     * URL: /accountant/fee-save
     */
    @PostMapping("/fee-save")
    public String handleFeeSave(@ModelAttribute("hoaDon") HoaDon hoaDon, 
                                @RequestParam("maHo") String maHo, 
                                Authentication auth,
                                RedirectAttributes redirectAttributes) {
        
        DoiTuong currentUser = getCurrentUser(auth);
        
        try {
            hoaDonService.saveOrUpdateHoaDon(hoaDon, maHo, currentUser); 
            
            String message = (hoaDon.getMaHoaDon() == null) 
                             ? "Tạo mới Hóa đơn thành công!" 
                             : "Cập nhật Hóa đơn #" + hoaDon.getMaHoaDon() + " thành công!";
            
            redirectAttributes.addFlashAttribute("successMessage", message);
            return "redirect:/accountant/fees";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/accountant/fee-form?id=" + (hoaDon.getMaHoaDon() != null ? hoaDon.getMaHoaDon() : "");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            return "redirect:/accountant/fees";
        }
    }

    /**
     * Xử lý Xóa Hóa đơn (POST).
     * URL: /accountant/fee-delete
     */
    @PostMapping("/fee-delete")
    public String handleDeleteFee(@RequestParam("id") Integer maHoaDon, 
                                  RedirectAttributes redirectAttributes) {
        try {
            hoaDonService.deleteHoaDon(maHoaDon); 
            redirectAttributes.addFlashAttribute("successMessage", "Xóa Hóa đơn #" + maHoaDon + " thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa Hóa đơn #" + maHoaDon + ": " + e.getMessage());
        }
        return "redirect:/accountant/fees";
    }

    // =======================================================
    // BÁO CÁO TÀI CHÍNH VÀ XÁC NHẬN THANH TOÁN
    // =======================================================
    
    /**
     * Chức năng Xác nhận Thanh Toán
     * URL: /accountant/fee-confirm
     */
    @PostMapping("/fee-confirm")
    public String handleFeeConfirm(@RequestParam("maHoaDon") Integer maHoaDon, 
                                   Authentication auth,
                                   RedirectAttributes redirectAttributes) {
        DoiTuong currentUser = getCurrentUser(auth);
        
        try {
            hoaDonService.confirmPayment(maHoaDon, currentUser); 
            redirectAttributes.addFlashAttribute("successMessage", "Xác nhận thanh toán Hóa đơn #" + maHoaDon + " thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống khi xác nhận: " + e.getMessage());
        }
        return "redirect:/accountant/fees"; 
    }
    /**
     * Chức năng Từ Chối Xác nhận Thanh Toán
     * URL: /accountant/fee-reject
     */
    @PostMapping("/fee-reject")
    public String handleFeeReject(@RequestParam("maHoaDon") Integer maHoaDon, 
                                  Authentication auth,
                                  RedirectAttributes redirectAttributes) {
        DoiTuong currentUser = getCurrentUser(auth);
        
        try {
            hoaDonService.rejectPayment(maHoaDon, currentUser); 
            redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối xác nhận thanh toán Hóa đơn #" + maHoaDon + ". Hóa đơn chuyển về trạng thái 'Chưa thanh toán'.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống khi từ chối xác nhận: " + e.getMessage());
        }
        return "redirect:/accountant/fees"; 
    }
    /**
     * Báo Cáo Tài Chính (Financial Report) & Lịch Sử Giao Dịch
     * URL: /accountant/reports/financial
     */
    @GetMapping("/reports/financial")
    public String showFinancialReports(Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));
        
        // Thống kê cơ bản
        model.addAttribute("stats", hoaDonService.getAccountantStats());
        
        // Lịch sử giao dịch (Hóa đơn đã thanh toán)
        List<HoaDon> paidInvoices = hoaDonService.getAllPaidHoaDon(); 
        model.addAttribute("paidInvoices", paidInvoices);
        
        return "financial-report-accountant"; // Tên file Thymeleaf mới
    }
}