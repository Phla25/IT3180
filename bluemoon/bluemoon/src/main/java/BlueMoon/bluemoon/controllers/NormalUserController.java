package BlueMoon.bluemoon.controllers;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import BlueMoon.bluemoon.entities.DoiTuong;
import BlueMoon.bluemoon.entities.HoGiaDinh;
import BlueMoon.bluemoon.models.HoGiaDinhDTO;
import BlueMoon.bluemoon.models.HoaDonStatsDTO;
import BlueMoon.bluemoon.services.HoaDonService;
import BlueMoon.bluemoon.services.NguoiDungService;
import BlueMoon.bluemoon.services.ThanhVienHoService;

@Controller
public class NormalUserController {

    @Autowired
    private NguoiDungService nguoiDungService;

    @Autowired
    private ThanhVienHoService thanhVienHoService;
    /**
     * Helper: Lấy đối tượng DoiTuong hiện tại
     * Giả sử username của principal là CCCD (đã được cấu hình trong UserDetailsService)
     */
    private DoiTuong getCurrentUser(Authentication auth) {
        String cccd = auth.getName(); // Lấy CCCD từ principal/username
        Optional<DoiTuong> userOpt = nguoiDungService.timNguoiDungThuongTheoCCCD(cccd);
        return userOpt.orElse(null); 
    }
    // Trong NormalUserController.java

    // ... (các imports)
    // @Autowired private ThanhVienHoService thanhVienHoService; // Đã có
    @Autowired private HoaDonService hoaDonService; // <-- Thêm Service

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
        // Giả định: Service TVH có thể trả về Optional<HoGiaDinh>
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
    
        // B4: MOCK Dữ liệu còn lại để tránh lỗi parsing
        model.addAttribute("dichVuStats", new Object());
        model.addAttribute("suCoStats", new Object());
        model.addAttribute("thongBaoStats", new Object());
        model.addAttribute("hoGiaDinhStats", new Object());

        return "dashboard-resident";
    }
}
