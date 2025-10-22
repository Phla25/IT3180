package BlueMoon.bluemoon.controllers;

import java.math.BigDecimal;
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

import BlueMoon.bluemoon.daos.BaoCaoSuCoDAO;
import BlueMoon.bluemoon.daos.HoGiaDinhDAO;
import BlueMoon.bluemoon.daos.HoaDonDAO;
import BlueMoon.bluemoon.entities.BaoCaoSuCo;
import BlueMoon.bluemoon.entities.DoiTuong;
import BlueMoon.bluemoon.services.CuDanService;
import BlueMoon.bluemoon.services.NguoiDungService;
import BlueMoon.bluemoon.utils.AccountStatus;
import BlueMoon.bluemoon.utils.Gender;
import BlueMoon.bluemoon.utils.IncidentStatus;
import BlueMoon.bluemoon.utils.InvoiceStatus;
import BlueMoon.bluemoon.utils.PriorityLevel;
import BlueMoon.bluemoon.utils.ResidentStatus;
import BlueMoon.bluemoon.utils.UserRole;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private NguoiDungService nguoiDungService;

    private DoiTuong getCurrentUser(Authentication auth) {
        String id = auth.getName();
        Optional<DoiTuong> userOpt = nguoiDungService.timBanQuanTriTheoID(id);
        return userOpt.orElse(null); 
    }

    @Autowired private CuDanService cuDanService;
    @Autowired private HoGiaDinhDAO hoGiaDinhDAO;
    @Autowired private BaoCaoSuCoDAO suCoDAO;
    @Autowired private HoaDonDAO hoaDonDAO;

    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model, Authentication auth) {
        
        DoiTuong user = getCurrentUser(auth);
        if (user == null) {
            return "redirect:/login?error=notfound";
        }
        // ⚠️ Đảm bảo user không null (Spring Security thường lo phần này)
        model.addAttribute("user", user);

        // 1. Thống kê chung
        model.addAttribute("tongCuDan", cuDanService.layDanhSachCuDan().size());
        model.addAttribute("tongHoGiaDinh", hoGiaDinhDAO.countAll());
        
        long suCoChuaXuLy = suCoDAO.countByTrangThai(IncidentStatus.moi_tiep_nhan);
        long suCoDangXuLy = suCoDAO.countByTrangThai(IncidentStatus.dang_xu_ly);
        model.addAttribute("suCoChuaXuLy", suCoChuaXuLy + suCoDangXuLy);
        
        // Dùng Optional.orElse để tránh NPE nếu Service trả về null
        BigDecimal tongThu = hoaDonDAO.sumSoTienByTrangThai(InvoiceStatus.da_thanh_toan);
        model.addAttribute("doanhThuThang", tongThu); 

        // 2. Thống kê nhanh (Tỷ lệ)
        long tongSuCo = suCoDAO.countAll(); // Cần thêm phương thức này
        long suCoDaXuLy = suCoDAO.countByTrangThai(IncidentStatus.da_hoan_thanh);
        
        // Tính tỷ lệ an toàn, sử dụng BigDecimal để tránh chia cho 0
        int tyLeSuCoDaXuLy = (tongSuCo > 0) ? (int)((suCoDaXuLy * 100) / tongSuCo) : 0;
        model.addAttribute("tyLeSuCoDaXuLy", tyLeSuCoDaXuLy);
        // ... Các tỷ lệ khác ...

        // 3. Danh sách Sự Cố Cần Xử Lý Gấp (Luôn trả về List, có thể rỗng)
        List<BaoCaoSuCo> suCoCanXuLy = suCoDAO.findByMucDoUuTien(PriorityLevel.cao);
        model.addAttribute("suCoCanXuLy", suCoCanXuLy); // Dùng List, Thymeleaf sẽ tự xử lý list rỗng

        return "dashboard-admin";
    }
    // Trong AdminController.java

    // @Autowired private CuDanService cuDanService; // Đã có

    @GetMapping("/resident-list")
    public String showResidentList(Model model, 
                               @RequestParam(required = false) String keyword,
                               @RequestParam(required = false) ResidentStatus trangThaiDanCu,
                               Authentication auth) {
    
        // 1. Lấy thông tin người dùng đang đăng nhập (header)
        DoiTuong user = getCurrentUser(auth); 
        if (user == null) {
            return "redirect:/login?error=auth";
        }
        model.addAttribute("user", user);

        // 2. Lấy danh sách đối tượng (có áp dụng tìm kiếm/lọc)
        // Nếu có tham số tìm kiếm, gọi hàm lọc; nếu không, lấy tất cả.
        List<DoiTuong> danhSachDoiTuong;
        if (keyword != null || trangThaiDanCu != null) {
            danhSachDoiTuong = cuDanService.timKiemvaLoc(keyword, trangThaiDanCu);
        } else {
            danhSachDoiTuong = cuDanService.layDanhSachCuDan();
        }

        // 3. Thêm danh sách vào Model
        model.addAttribute("residents", danhSachDoiTuong);
    
        // 4. (Tùy chọn) Thêm thông tin phân trang
        model.addAttribute("totalResidents", danhSachDoiTuong.size()); // Giả định không phân trang
    
        return "residents"; // Giả định tên file Thymeleaf là residents-list.html
    }
    /**
     * HIỂN THỊ FORM (GET)
     * Đường dẫn: /admin/resident-add
     */
    @GetMapping("/resident-add")
    public String showAddResidentForm(Model model, Authentication auth) {
        // Lấy user cho header
        model.addAttribute("user", getCurrentUser(auth));
        
        // Cần truyền một đối tượng rỗng để binding form (th:object)
        model.addAttribute("newResident", new DoiTuong());

        // Truyền các giá trị Enum để hiển thị trong dropdown (nếu cần)
        model.addAttribute("genders", Gender.values());
        
        return "resident-add"; // Tên file Thymeleaf: resident-add.html
    }

    @PostMapping("/resident-add")
    public String addNewResident(@ModelAttribute("newResident") DoiTuong newResident,
                             RedirectAttributes redirectAttributes) {
        try {
            // 1. Service phải tự động thiết lập các trường mặc định:
            //    matKhau (random), vaiTro (khong_dung_he_thong), laCuDan (true), v.v.
            //    (CuDanService.themCuDan của bạn đã xử lý phần lớn, nhưng cần đảm bảo matKhau được tạo)
            
            // 2. Tùy chọn: Đặt Email và SĐT thành null nếu không được nhập, nhưng hiện tại form yêu cầu BẮT BUỘC. 
            //    Nếu muốn không bắt buộc, ta phải chỉnh form. Hiện tại, hãy để Service xử lý.

            DoiTuong savedResident = cuDanService.themCuDan(newResident);

            redirectAttributes.addFlashAttribute("successMessage", 
                "Đã thêm thông tin cư dân " + savedResident.getHoVaTen() + " thành công.");
        
            return "redirect:/admin/resident-list"; 
        
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/admin/resident-add"; 
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống khi lưu: " + e.getMessage());
            return "redirect:/admin/resident-add"; 
        }
    }

    /**
     * XỬ LÝ XÓA MỀM (Chuyển đi/Mất)
     * Đường dẫn: /admin/resident-delete
     * Phương thức: POST (hoặc GET đơn giản)
     */
    @GetMapping("/resident-delete") // Dùng GET cho đơn giản với link href/redirect
    public String deleteResident(@RequestParam String cccd, 
                             @RequestParam ResidentStatus lyDo, 
                             RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra lý do hợp lệ (chỉ chấp nhận roi_di hoặc da_chet)
            if (lyDo != ResidentStatus.roi_di && lyDo != ResidentStatus.da_chet) {
             throw new IllegalArgumentException("Lý do xóa không hợp lệ.");
            }
        
            cuDanService.xoaCuDan(cccd, lyDo);

            redirectAttributes.addFlashAttribute("successMessage", 
                "Đã cập nhật trạng thái cư dân " + cccd + " thành công (Lý do: " + lyDo.getDbValue() + ").");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi xóa cư dân: " + e.getMessage());
        }
        return "redirect:/admin/resident-list";
    }
    // Trong AdminController.java

    // 1. GET: Hiển thị Form với Dữ liệu Cũ
    @GetMapping("/resident-edit")
    public String showEditResidentForm(@RequestParam String cccd, Model model, Authentication auth) {
        DoiTuong user = getCurrentUser(auth);
        if (user == null) {
            return "redirect:/login?error=auth";
        }
        model.addAttribute("user", user);

        // Lấy thông tin cư dân cần chỉnh sửa
        Optional<DoiTuong> residentOpt = cuDanService.timCuDanTheoCCCD(cccd);
    
        if (residentOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Không tìm thấy cư dân với CCCD: " + cccd);
            return "redirect:/admin/resident-list";
        }   
    
        DoiTuong residentToEdit = residentOpt.get();
    
        // Đặt đối tượng vào model để binding form (th:object)
        model.addAttribute("resident", residentToEdit);
    
        // Truyền Enum (cho dropdown)
        model.addAttribute("genders", Gender.values());
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("accountStatuses", AccountStatus.values());
        model.addAttribute("residentStatuses", ResidentStatus.values());

        return "resident-edit"; 
    }


    // 2. POST: Xử lý Cập nhật
    @PostMapping("/resident-edit")
    public String updateResident(@ModelAttribute("resident") DoiTuong residentCapNhat,
                                 @RequestParam(value = "matKhauMoi", required = false) String matKhauMoi,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Lấy CCCD từ đối tượng form đã bind (CCCD là trường cố định)
            String cccd = residentCapNhat.getCccd(); 

            // 1. Cập nhật thông tin cơ bản (họ tên, ngày sinh, giới tính, SĐT, email, etc.)
            cuDanService.capNhatCuDan(cccd, residentCapNhat);
        
            // 2. Xử lý đổi mật khẩu (nếu có)
            if (matKhauMoi != null && !matKhauMoi.trim().isEmpty()) {
                 // LƯU Ý: Cần có phương thức đổi mật khẩu cho Admin trong NguoiDungService (Admin bỏ qua mật khẩu cũ)
                 nguoiDungService.resetMatKhau(cccd, matKhauMoi); // Giả sử hàm này được overloaded hoặc sử dụng cho Admin
            }

            redirectAttributes.addFlashAttribute("successMessage", 
                "Cập nhật thông tin cư dân " + residentCapNhat.getHoVaTen() + " thành công!");
        
            return "redirect:/admin/resident-list";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi cập nhật: " + e.getMessage());
            return "redirect:/admin/resident-edit?cccd=" + residentCapNhat.getCccd();
        }
    }
}