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
import BlueMoon.bluemoon.entities.HoGiaDinh;
import BlueMoon.bluemoon.entities.TaiSanChungCu;
import BlueMoon.bluemoon.entities.ThanhVienHo;
import BlueMoon.bluemoon.services.CuDanService;
import BlueMoon.bluemoon.services.HoGiaDinhService;
import BlueMoon.bluemoon.services.NguoiDungService;
import BlueMoon.bluemoon.services.TaiSanChungCuService;
import BlueMoon.bluemoon.utils.AccountStatus;
import BlueMoon.bluemoon.utils.Gender;
import BlueMoon.bluemoon.utils.HouseholdStatus;
import BlueMoon.bluemoon.utils.IncidentStatus;
import BlueMoon.bluemoon.utils.InvoiceStatus;
import BlueMoon.bluemoon.utils.PriorityLevel;
import BlueMoon.bluemoon.utils.ResidentStatus;
import BlueMoon.bluemoon.utils.TerminationReason;
import BlueMoon.bluemoon.utils.UserRole;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private NguoiDungService nguoiDungService;

    @Autowired private HoGiaDinhService hoGiaDinhService;

    private DoiTuong getCurrentUser(Authentication auth) {
        String id = auth.getName();
        Optional<DoiTuong> userOpt = nguoiDungService.timBanQuanTriTheoID(id);
        return userOpt.orElse(null); 
    }

    @Autowired private CuDanService cuDanService;
    @Autowired private HoGiaDinhDAO hoGiaDinhDAO;
    @Autowired private BaoCaoSuCoDAO suCoDAO;
    @Autowired private HoaDonDAO hoaDonDAO;
    @Autowired private TaiSanChungCuService taiSanChungCuService;

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
    @GetMapping("/profile")
    public String showAdminProfile(Model model, Authentication auth) {
        
        // 1. Lấy thông tin người dùng đang đăng nhập (Ban Quản Trị)
        DoiTuong user = getCurrentUser(auth); 
        
        if (user == null) {
            // Trường hợp lỗi (ví dụ: Session hết hạn hoặc không tìm thấy user)
            return "redirect:/login?error=auth";
        }

        // 2. Thêm đối tượng user vào Model để hiển thị trong Thymeleaf
        model.addAttribute("user", user);

        // 3. Trả về tên file Thymeleaf (profile.html trong thư mục template/admin/)
        return "profile-admin"; // Hoặc "admin/profile" tùy theo cấu trúc thư mục của bạn
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

    /**
     * HIỂN THỊ CHI TIẾT CƯ DÂN (GET)
     * Đường dẫn: /admin/resident-details?cccd={cccd}
     */
    @GetMapping("/resident-details")
    @Transactional // Đảm bảo Lazy Loading của ThanhVienHo hoạt động
    public String showResidentDetails(@RequestParam("cccd") String cccd, Model model, Authentication auth) {
        DoiTuong userAdmin = getCurrentUser(auth); 
        if (userAdmin == null) {
            return "redirect:/login?error=auth";
        }
        model.addAttribute("user", userAdmin);

        // 1. Lấy thông tin cư dân chính
        DoiTuong resident = cuDanService.timCuDanTheoCCCD(cccd)
            .orElse(null);
        
        if (resident == null) {
            model.addAttribute("errorMessage", "Không tìm thấy cư dân với CCCD: " + cccd);
            return "redirect:/admin/resident-list";
        }
        model.addAttribute("resident", resident);

        // 2. Lấy mối quan hệ hộ gia đình hiện tại (ThanhVienHo)
        // Cần inject ThanhVienHoService
        // ⚠️ GIẢ ĐỊNH: Bạn sẽ inject ThanhVienHoService vào Controller này.
        // @Autowired private ThanhVienHoService thanhVienHoService; 
        
        // Dùng HoGiaDinhService để lấy thông tin liên quan đến hộ (nếu có)
        Optional<ThanhVienHo> tvhOpt = hoGiaDinhService.getThanhVienHoCurrentByCccd(cccd);
        
        if (tvhOpt.isPresent()) {
            ThanhVienHo tvh = tvhOpt.get();
            model.addAttribute("currentHousehold", tvh.getHoGiaDinh()); // Hộ gia đình
            model.addAttribute("memberRelation", tvh);                   // Chi tiết quan hệ (Chủ hộ, Quan hệ, Ngày bắt đầu)
            
            // Lấy thông tin căn hộ (nếu có)
            Optional<TaiSanChungCu> apartmentOpt = hoGiaDinhService.getApartmentByHousehold(tvh.getHoGiaDinh().getMaHo());
            model.addAttribute("apartment", apartmentOpt.orElse(null));
        } else {
            model.addAttribute("currentHousehold", null);
            model.addAttribute("memberRelation", null);
            model.addAttribute("apartment", null);
        }

        return "resident-details"; // Trỏ đến file Thymeleaf mới
    }

    // =======================================================
    // PROFILE EDIT / CHANGE PASSWORD
    // =======================================================
    
    // Hiển thị form Đổi Mật Khẩu
    @GetMapping("/change-password")
    public String showAdminChangePasswordForm(Model model, Authentication auth) {
        DoiTuong user = getCurrentUser(auth); 
        if (user == null) {
            return "redirect:/login?error=auth";
        }
        model.addAttribute("user", user);
        return "change-password-admin"; 
    }

    // Xử lý POST request Đổi Mật Khẩu
    @PostMapping("/change-password")
    public String handleAdminChangePassword(@RequestParam("matKhauCu") String matKhauCu,
                                            @RequestParam("matKhauMoi") String matKhauMoi,
                                            @RequestParam("xacNhanMatKhau") String xacNhanMatKhau,
                                            Authentication auth,
                                            RedirectAttributes redirectAttributes) {
        
        DoiTuong currentUser = getCurrentUser(auth); 
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi xác thực người dùng.");
            return "redirect:/admin/profile";
        }

        if (!matKhauMoi.equals(xacNhanMatKhau)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu mới và xác nhận mật khẩu không khớp.");
            return "redirect:/admin/change-password";
        }
        
        try {
            nguoiDungService.doiMatKhau(currentUser.getCccd(), matKhauCu, matKhauMoi);
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
            return "redirect:/logout"; 
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/change-password";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            return "redirect:/admin/change-password";
        }
    }

    // Hiển thị form Cập Nhật Thông Tin Cá Nhân
    @GetMapping("/profile/edit")
    public String showAdminEditProfileForm(Model model, Authentication auth) {
        DoiTuong user = getCurrentUser(auth); 
        if (user == null) {
            return "redirect:/login?error=auth";
        }
        model.addAttribute("user", user); 
        model.addAttribute("genders", Gender.values()); // Để hiển thị Enum giới tính
        return "edit-profile-admin"; 
    }

    // Xử lý POST request Cập Nhật Thông Tin Cá Nhân
    @PostMapping("/profile/edit")
    public String handleAdminEditProfile(@ModelAttribute("user") DoiTuong doiTuongCapNhat,
                                        Authentication auth,
                                        RedirectAttributes redirectAttributes) {
        
        DoiTuong currentUser = getCurrentUser(auth); 
        if (currentUser == null || !currentUser.getCccd().equals(doiTuongCapNhat.getCccd())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi xác thực người dùng.");
            return "redirect:/admin/profile";
        }
        
        try {
            // Sử dụng hàm đã thêm trong NguoiDungService
            nguoiDungService.capNhatThongTinNguoiDung(doiTuongCapNhat);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin cá nhân thành công!");
            return "redirect:/admin/profile";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/profile/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống khi cập nhật: " + e.getMessage());
            return "redirect:/admin/profile/edit";
        }
    }
    // =======================================================
    // QUẢN LÝ HỘ GIA ĐÌNH
    // =======================================================

    /**
     * Hiển thị danh sách hộ gia đình
     */
    @GetMapping("/household-list")
    public String showHouseholdList(Model model, 
                                    @RequestParam(required = false) String keyword,
                                    Authentication auth) {
        
        model.addAttribute("user", getCurrentUser(auth));
        
        List<HoGiaDinh> households = hoGiaDinhService.getAllHouseholds(keyword);
        
        model.addAttribute("households", households);
        model.addAttribute("keyword", keyword); // Để giữ lại giá trị tìm kiếm trên form
        
        return "household-list"; // Tên file Thymeleaf: household-list.html
    }

    /**
     * Hiển thị form thêm hộ gia đình mới (GET)
     */
    @GetMapping("/household-add")
    public String showAddHouseholdForm(Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));
        model.addAttribute("newHousehold", new HoGiaDinh());
        model.addAttribute("householdStatuses", HouseholdStatus.values());
        
        // Thêm DTO hoặc RequestParam để nhập CCCD Chủ hộ
        // Giả định dùng RequestParam: chuHoCccd và quanHe
        
        return "household-add"; // Tên file Thymeleaf: household-add.html
    }

    /**
     * Xử lý thêm hộ gia đình mới (POST)
     */
    @PostMapping("/household-add")
    public String handleAddHousehold(@ModelAttribute("newHousehold") HoGiaDinh hoGiaDinh,
                                     @RequestParam("chuHoCccd") String chuHoCccd,
                                     @RequestParam(value = "quanHeVoiChuHo", defaultValue = "Chủ hộ") String quanHe,
                                     RedirectAttributes redirectAttributes) {
        try {
            hoGiaDinhService.themHoGiaDinh(hoGiaDinh, chuHoCccd, quanHe);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm Hộ gia đình " + hoGiaDinh.getTenHo() + " thành công!");
            return "redirect:/admin/household-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/admin/household-add";
        }
    }

    /**
     * Xem chi tiết hộ gia đình, danh sách thành viên và căn hộ đang ở (GET)
     */
    @GetMapping("/household-detail")
    @Transactional // Cần thêm @Transactional để đảm bảo lazy loading List<ThanhVienHo> hoạt động
    public String showHouseholdDetail(@RequestParam("maHo") String maHo, Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));
        
        HoGiaDinh hgd = hoGiaDinhService.getHouseholdById(maHo)
            .orElse(null);
        
        if (hgd == null) {
            model.addAttribute("errorMessage", "Không tìm thấy Hộ gia đình.");
            return "redirect:/admin/household-list";
        }

        // 1. Tải danh sách thành viên đang hoạt động
        // Dùng Lazy loading List<ThanhVienHo> trên Entity HGD (Cần @Transactional)
        List<ThanhVienHo> thanhVienList = hgd.getThanhVienHoList().stream()
            .filter(tvh -> tvh.getNgayKetThuc() == null) 
            .toList();
        
        // 2. Lấy thông tin căn hộ chính
        Optional<TaiSanChungCu> apartmentOpt = hoGiaDinhService.getApartmentByHousehold(maHo);
        
        // 3. Thêm dữ liệu vào Model
        model.addAttribute("household", hgd);
        model.addAttribute("members", thanhVienList);
        model.addAttribute("apartment", apartmentOpt.orElse(null)); // Thêm căn hộ (hoặc null)
        model.addAttribute("terminationReasons", TerminationReason.values());
        
        return "household-details"; 
    }
    /**
     * HIỂN THỊ FORM TÁCH HỘ (GET)
     * Đường dẫn: /admin/household-split
     * Yêu cầu: maHo (Hộ cũ)
     */
    @GetMapping("/household-split")
    @Transactional
    public String showSplitHouseholdForm(@RequestParam("maHo") String maHoCu, Model model, Authentication auth) {
        DoiTuong user = getCurrentUser(auth); 
        if (user == null) {
            return "redirect:/login?error=auth";
        }
        
        HoGiaDinh hgdCu = hoGiaDinhService.getHouseholdById(maHoCu)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Hộ gia đình cũ."));

        // Lấy danh sách thành viên hiện tại của hộ cũ
        List<DoiTuong> members = hgdCu.getThanhVienHoList().stream()
            .filter(tvh -> tvh.getNgayKetThuc() == null)
            .map(ThanhVienHo::getDoiTuong)
            .toList();
        
        model.addAttribute("user", user);
        model.addAttribute("household", hgdCu);
        model.addAttribute("members", members); // Danh sách thành viên để chọn
        model.addAttribute("newHousehold", new HoGiaDinh()); // DTO cho thông tin Hộ mới
        
        return "household-split"; // Tên file Thymeleaf mới
    }

    /**
     * XỬ LÝ TÁCH HỘ (POST)
     * Đường dẫn: /admin/household-split
     */
    @PostMapping("/household-split")
    public String handleSplitHousehold(@RequestParam("maHoCu") String maHoCu,
                                       @RequestParam("tenHoMoi") String tenHoMoi,
                                       @RequestParam("chuHoMoiCccd") String chuHoMoiCccd,
                                       @RequestParam("cccdDuocTach") List<String> cccdThanhVienDuocTach, // List CCCD được chọn
                                       RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra số lượng thành viên tối thiểu
            if (cccdThanhVienDuocTach.isEmpty()) {
                throw new IllegalArgumentException("Vui lòng chọn ít nhất một thành viên để tách hộ.");
            }
            
            // Gọi logic Service Tách Hộ
            HoGiaDinh hoGiaDinhMoi = hoGiaDinhService.tachHo(
                maHoCu, 
                cccdThanhVienDuocTach, 
                chuHoMoiCccd, 
                tenHoMoi
            );
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Tách hộ thành công! Đã tạo Hộ gia đình mới: " + hoGiaDinhMoi.getTenHo());
            
            return "redirect:/admin/household-detail?maHo=" + hoGiaDinhMoi.getMaHo();

        } catch (IllegalStateException e) {
            // Lỗi khi Chủ hộ cũ bị tách nhưng hộ cũ vẫn còn thành viên
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/household-detail?maHo=" + maHoCu;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi tách hộ: " + e.getMessage());
            return "redirect:/admin/household-split?maHo=" + maHoCu; // Quay lại form Tách Hộ
        }
    }
    /**
    * HIỂN THỊ FORM THÊM THÀNH VIÊN (GET)
     * Đường dẫn: /admin/household-member-add
     * Yêu cầu: maHo (Mã hộ cần thêm)
    */
    @GetMapping("/household-member-add")
    public String showAddMemberForm(@RequestParam("maHo") String maHo, Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));
    
        // Kiểm tra hộ gia đình tồn tại
        HoGiaDinh hgd = hoGiaDinhService.getHouseholdById(maHo)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Hộ gia đình với Mã Hộ: " + maHo));

        model.addAttribute("household", hgd);
        // Truyền DTO/Form object nếu cần, nhưng ở đây ta dùng @RequestParam đơn giản hơn
    
        return "household-member-add"; // Trỏ đến file Thymeleaf mới
    }

    /**
     * XỬ LÝ THÊM THÀNH VIÊN (POST)
     * Đường dẫn: /admin/household-member-add
     */
    @PostMapping("/household-member-add")
    public String handleAddMember(@RequestParam("maHo") String maHo,
                                  @RequestParam("cccdThanhVien") String cccdThanhVien,
                                  @RequestParam("quanHe") String quanHe,
                                  RedirectAttributes redirectAttributes) {
        try {
            // Mặc định laChuHo = false khi thêm mới
            // LyDoKetThuc = chuyen_ho (sẽ được ghi đè nếu thành viên này đang ở hộ khác)
            hoGiaDinhService.themThanhVien(maHo, cccdThanhVien, false, quanHe, null);
        
            redirectAttributes.addFlashAttribute("successMessage", 
                "Đã thêm thành viên CCCD " + cccdThanhVien + " vào Hộ " + maHo + " thành công.");
        
            return "redirect:/admin/household-detail?maHo=" + maHo;

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/admin/household-member-add?maHo=" + maHo;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống khi thêm thành viên: " + e.getMessage());
            return "redirect:/admin/household-member-add?maHo=" + maHo;
        }
    }

    /**
     * HIỂN THỊ FORM CHUYỂN CHỦ HỘ (GET)
     * Đường dẫn: /admin/household-change-owner
     * Yêu cầu: maHo (Mã hộ cần chuyển Chủ hộ)
     */
    @GetMapping("/household-change-owner")
    @Transactional // Cần load danh sách thành viên
    public String showChangeChuHoForm(@RequestParam("maHo") String maHo, Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));
    
        HoGiaDinh hgd = hoGiaDinhService.getHouseholdById(maHo)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Hộ gia đình với Mã Hộ: " + maHo));

        // Lấy danh sách thành viên hiện tại (trừ Chủ hộ hiện tại)
        List<DoiTuong> members = hgd.getThanhVienHoList().stream()
            .filter(tvh -> tvh.getNgayKetThuc() == null && !tvh.getLaChuHo())
            .map(ThanhVienHo::getDoiTuong)
            .toList();

        model.addAttribute("household", hgd);
        model.addAttribute("members", members);
    
        return "household-change-owner"; // Trỏ đến file Thymeleaf mới
    }

    /**
     * XỬ LÝ CHUYỂN CHỦ HỘ (POST)
     * Đường dẫn: /admin/household-change-ch
     */
    @PostMapping("/household-change-owner")
    public String handleChangeChuHo(@RequestParam("maHo") String maHo,
                                     @RequestParam("cccdChuHoMoi") String cccdChuHoMoi,
                                     RedirectAttributes redirectAttributes) {
        try {
            hoGiaDinhService.capNhatChuHo(maHo, cccdChuHoMoi);
        
            redirectAttributes.addFlashAttribute("successMessage", 
                "Đã chuyển Chủ hộ cho Hộ " + maHo + " thành công (CCCD mới: " + cccdChuHoMoi + ").");
        
            return "redirect:/admin/household-detail?maHo=" + maHo;

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/admin/household-change-owner?maHo=" + maHo;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống khi chuyển Chủ hộ: " + e.getMessage());
            return "redirect:/admin/household-change-owner?maHo=" + maHo;
        }
    }
    // =======================================================
    // QUẢN LÝ CĂN HỘ (APARTMENTS)
    // =======================================================

    /**
     * Hiển thị danh sách Căn Hộ (GET) có hỗ trợ phân loại.
     */
    @GetMapping("/apartment-list")
    public String showApartmentList(
            Model model, 
            Authentication auth,
            @RequestParam(required = false) String keyword, // Giả định có tìm kiếm theo tên
            @RequestParam(required = false) BlueMoon.bluemoon.utils.AssetStatus status,
            @RequestParam(required = false) BigDecimal minArea,
            @RequestParam(required = false) BigDecimal maxArea,
            @RequestParam(required = false) BigDecimal minValue,
            @RequestParam(required = false) BigDecimal maxValue
        ) {
        model.addAttribute("user", getCurrentUser(auth));
        
        // Lấy danh sách căn hộ dựa trên các bộ lọc
        List<TaiSanChungCu> apartments;
        
        // Chú ý: Hiện tại DAO chỉ hỗ trợ lọc đơn (Area, Value, Status).
        // Ta ưu tiên lọc phức tạp hơn trước.
        if (minArea != null && maxArea != null && minArea.compareTo(maxArea) <= 0) {
            apartments = taiSanChungCuService.getApartmentsByAreaRange(minArea, maxArea);
        } else if (minValue != null && maxValue != null && minValue.compareTo(maxValue) <= 0) {
            apartments = taiSanChungCuService.getApartmentsByValueRange(minValue, maxValue);
        } else if (status != null) {
            apartments = taiSanChungCuService.getApartmentsByStatus(status);
        } else {
            // Nếu không có bộ lọc nào, trả về tất cả
            apartments = taiSanChungCuService.getAllApartments();
        }
        
        // Nếu có keyword, cần thêm logic tìm kiếm theo tên/mã thủ công tại đây nếu Service không hỗ trợ
        // (Bỏ qua logic keyword để tập trung vào phân loại chính)

        model.addAttribute("apartments", apartments);
        // Lưu trữ các giá trị lọc để giữ lại trên form
        model.addAttribute("currentStatus", status); 
        model.addAttribute("minArea", minArea);
        model.addAttribute("maxArea", maxArea);
        model.addAttribute("minValue", minValue);
        model.addAttribute("maxValue", maxValue);
        model.addAttribute("assetStatuses", BlueMoon.bluemoon.utils.AssetStatus.values());

        return "apartment-list-admin"; 
    }

    /**
     * Hiển thị form thêm Căn Hộ mới (GET)
     */
    @GetMapping("/apartment-add")
    public String showAddApartmentForm(Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));
        model.addAttribute("newApartment", new TaiSanChungCu());
        model.addAttribute("assetStatuses", BlueMoon.bluemoon.utils.AssetStatus.values());
        model.addAttribute("households", hoGiaDinhService.getAllHouseholds()); // Lấy danh sách Hộ gia đình để liên kết
    
        return "apartment-add";
    }

    /**
     * Xử lý thêm Căn Hộ mới (POST)
     */
    @PostMapping("/apartment-add")
    public String handleAddApartment(@ModelAttribute("newApartment") TaiSanChungCu apartment,
                                     @RequestParam(value = "maHoLienKet", required = false) String maHoLienKet,
                                     RedirectAttributes redirectAttributes) {
        try {
            taiSanChungCuService.themCanHo(apartment, maHoLienKet);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Thêm Căn hộ " + apartment.getTenTaiSan() + " thành công!");
            return "redirect:/admin/apartment-list";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/admin/apartment-add";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            return "redirect:/admin/apartment-add";
        }
    }

    /**
     * Hiển thị form chỉnh sửa Căn Hộ (GET)
     */
    @GetMapping("/apartment-edit")
    public String showEditApartmentForm(@RequestParam("maTaiSan") Integer maTaiSan, Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));
    
        TaiSanChungCu apartmentToEdit = taiSanChungCuService.getApartmentById(maTaiSan)
            .orElse(null);

        if (apartmentToEdit == null) {
            model.addAttribute("errorMessage", "Không tìm thấy Căn hộ.");
            return "redirect:/admin/apartment-list";
        }

        model.addAttribute("apartment", apartmentToEdit);
        model.addAttribute("assetStatuses", BlueMoon.bluemoon.utils.AssetStatus.values());
        model.addAttribute("households", hoGiaDinhService.getAllHouseholds()); 
    
        return "apartment-edit";
    }

    /**
     * Xử lý cập nhật Căn Hộ (POST)
     */
    @PostMapping("/apartment-edit")
    public String handleEditApartment(@ModelAttribute("apartment") TaiSanChungCu apartment,
                                      @RequestParam("maTaiSan") Integer maTaiSan,
                                      @RequestParam(value = "maHoLienKet", required = false) String maHoLienKet,
                                      RedirectAttributes redirectAttributes) {
        try {
            taiSanChungCuService.capNhatCanHo(maTaiSan, apartment, maHoLienKet);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Cập nhật Căn hộ " + apartment.getTenTaiSan() + " thành công!");
            return "redirect:/admin/apartment-list";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/admin/apartment-edit?maTaiSan=" + maTaiSan;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            return "redirect:/admin/apartment-edit?maTaiSan=" + maTaiSan;
        }
    }

    /**
     * Xử lý xóa Căn Hộ (GET cho đơn giản)
     */
    @GetMapping("/apartment-delete")
    public String handleDeleteApartment(@RequestParam("maTaiSan") Integer maTaiSan, RedirectAttributes redirectAttributes) {
        try {
            taiSanChungCuService.xoaCanHo(maTaiSan);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa căn hộ Mã " + maTaiSan + " thành công.");
            return "redirect:/admin/apartment-list";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/admin/apartment-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống khi xóa: " + e.getMessage());
            return "redirect:/admin/apartment-list";
        }
    }
    /**
    * Xem chi tiết Căn Hộ (GET)
    */
    @GetMapping("/apartment-details")
    public String showAdminApartmentDetails(@RequestParam("maTaiSan") Integer maTaiSan, Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));

        TaiSanChungCu apartment = taiSanChungCuService.getApartmentById(maTaiSan)
            .orElse(null);
        
        if (apartment == null) {
            model.addAttribute("errorMessage", "Không tìm thấy Căn hộ với Mã Tài Sản: " + maTaiSan);
            return "redirect:/admin/apartment-list";
        }

        model.addAttribute("apartment", apartment);
    
        // Tùy chọn: Thêm danh sách thành viên hộ liên kết (nếu có)
        if (apartment.getHoGiaDinh() != null) {
            List<ThanhVienHo> members = apartment.getHoGiaDinh().getThanhVienHoList().stream()
                .filter(tvh -> tvh.getNgayKetThuc() == null) 
                .toList();
            model.addAttribute("members", members);
        } else {
             model.addAttribute("members", List.of());
        }

        return "apartment-details-admin";
    }
    // =======================================================
    // QUẢN LÝ TẤT CẢ TÀI SẢN (GENERAL ASSETS) - Bao gồm cả Căn Hộ
    // =======================================================

    /**
     * Hiển thị danh sách TẤT CẢ Tài Sản (GET) có hỗ trợ lọc theo loại và tìm kiếm.
     * URL: /admin/general-asset-list
     */
    @GetMapping("/general-asset-list")
    public String showGeneralAssetList(
            Model model, 
            Authentication auth,
            @RequestParam(required = false) String keyword, 
            @RequestParam(required = false) BlueMoon.bluemoon.utils.AssetType loaiTaiSan // Lọc theo loại
        ) {
        model.addAttribute("user", getCurrentUser(auth));
    
        // Lấy danh sách tài sản dựa trên các bộ lọc
        List<TaiSanChungCu> assets;
    
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Nếu có keyword, tìm kiếm theo tên, bỏ qua loaiTaiSan (hoặc bạn có thể thêm logic lọc kép)
            assets = taiSanChungCuService.findAssetsByFilters(keyword, null); 
        } else {
            // Nếu không có keyword, lọc theo loại tài sản (loaiTaiSan = null sẽ lấy tất cả)
            assets = taiSanChungCuService.getAllAssets(loaiTaiSan); 
        }
    
        model.addAttribute("assets", assets);
        // Lưu trữ các giá trị lọc để giữ lại trên form
        model.addAttribute("currentAssetType", loaiTaiSan);
        model.addAttribute("keyword", keyword);
        model.addAttribute("assetTypes", BlueMoon.bluemoon.utils.AssetType.values()); // Cần hiển thị tất cả loại
        model.addAttribute("assetStatuses", BlueMoon.bluemoon.utils.AssetStatus.values());

        return "general-asset-list"; // Tên file Thymeleaf mới
    }

    /**
     * Hiển thị form thêm Tài Sản Chung mới (GET)
     * URL: /admin/general-asset-add
     */
    @GetMapping("/general-asset-add")
    public String showAddGeneralAssetForm(Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));
        model.addAttribute("newAsset", new TaiSanChungCu());
        model.addAttribute("assetStatuses", BlueMoon.bluemoon.utils.AssetStatus.values());
        model.addAttribute("assetTypes", BlueMoon.bluemoon.utils.AssetType.values());
        // Lấy danh sách hộ gia đình để liên kết
        model.addAttribute("households", hoGiaDinhService.getAllHouseholds()); 

        return "general-asset-add"; 
    }

    /**
     * Xử lý thêm Tài Sản Chung mới (POST)
     * URL: /admin/general-asset-add
     */
    @PostMapping("/general-asset-add")
    public String handleAddGeneralAsset(@ModelAttribute("newAsset") TaiSanChungCu asset,
                                     @RequestParam(value = "maHoLienKet", required = false) String maHoLienKet,
                                     RedirectAttributes redirectAttributes) {
        try {
            TaiSanChungCu savedAsset = taiSanChungCuService.themTaiSanChung(asset, maHoLienKet);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Thêm Tài sản " + savedAsset.getTenTaiSan() + " (Mã: " + savedAsset.getMaTaiSan() + ") thành công!");
            return "redirect:/admin/general-asset-list";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/admin/general-asset-add";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            return "redirect:/admin/general-asset-add";
        }
    }

    /**
     * Hiển thị form chỉnh sửa Tài Sản Chung (GET)
     * URL: /admin/general-asset-edit?maTaiSan={id}
     */
    @GetMapping("/general-asset-edit")
    public String showEditGeneralAssetForm(@RequestParam("maTaiSan") Integer maTaiSan, Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));

        TaiSanChungCu assetToEdit = taiSanChungCuService.getAssetById(maTaiSan)
            .orElse(null);

        if (assetToEdit == null) {
            model.addAttribute("errorMessage", "Không tìm thấy Tài Sản.");
            return "redirect:/admin/general-asset-list";
        }

        model.addAttribute("asset", assetToEdit);
        model.addAttribute("assetStatuses", BlueMoon.bluemoon.utils.AssetStatus.values());
        model.addAttribute("assetTypes", BlueMoon.bluemoon.utils.AssetType.values());
        model.addAttribute("households", hoGiaDinhService.getAllHouseholds()); 

        return "general-asset-edit"; 
    }

    /**
     * Xử lý cập nhật Tài Sản Chung (POST)
     * URL: /admin/general-asset-edit
     */
    @PostMapping("/general-asset-edit")
    public String handleEditGeneralAsset(@ModelAttribute("asset") TaiSanChungCu asset,
                                      @RequestParam("maTaiSan") Integer maTaiSan,
                                      @RequestParam(value = "maHoLienKet", required = false) String maHoLienKet,
                                      RedirectAttributes redirectAttributes) {
        try {
            taiSanChungCuService.capNhatTaiSanChung(maTaiSan, asset, maHoLienKet);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Cập nhật Tài sản Mã " + maTaiSan + " thành công!");
            return "redirect:/admin/general-asset-list";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/admin/general-asset-edit?maTaiSan=" + maTaiSan;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            return "redirect:/admin/general-asset-edit?maTaiSan=" + maTaiSan;
        }
    }

    /**
     * Xử lý xóa Tài Sản Chung (GET cho đơn giản)
     * URL: /admin/general-asset-delete?maTaiSan={id}
     */
    @GetMapping("/general-asset-delete")
    public String handleDeleteGeneralAsset(@RequestParam("maTaiSan") Integer maTaiSan, RedirectAttributes redirectAttributes) {
        try {
            taiSanChungCuService.xoaTaiSanChung(maTaiSan);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa tài sản Mã " + maTaiSan + " thành công.");
            return "redirect:/admin/general-asset-list";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/admin/general-asset-list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống khi xóa: " + e.getMessage());
            return "redirect:/admin/general-asset-list";
        }
    }

    /**
    * Xem chi tiết Tài Sản Chung (GET)
    * URL: /admin/general-asset-details?maTaiSan={id}
    */
    @GetMapping("/general-asset-details")
    public String showAdminGeneralAssetDetails(@RequestParam("maTaiSan") Integer maTaiSan, Model model, Authentication auth) {
        model.addAttribute("user", getCurrentUser(auth));

        TaiSanChungCu asset = taiSanChungCuService.getAssetById(maTaiSan)
            .orElse(null);
    
        if (asset == null) {
            model.addAttribute("errorMessage", "Không tìm thấy Tài Sản với Mã Tài Sản: " + maTaiSan);
            return "redirect:/admin/general-asset-list";
        }

        model.addAttribute("asset", asset);

        // Tùy chọn: Thêm danh sách thành viên hộ liên kết (nếu có)
        if (asset.getHoGiaDinh() != null) {
            List<BlueMoon.bluemoon.entities.ThanhVienHo> members = asset.getHoGiaDinh().getThanhVienHoList().stream()
                .filter(tvh -> tvh.getNgayKetThuc() == null) 
                .toList();
            model.addAttribute("members", members);
        } else {
             model.addAttribute("members", List.of());
        }

        return "general-asset-details"; 
    }
}