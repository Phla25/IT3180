package BlueMoon.bluemoon.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import BlueMoon.bluemoon.entities.DoiTuong;
import BlueMoon.bluemoon.services.CuDanService;

@Controller
public class ResidentPageController {

    @Autowired
    private CuDanService cuDanService;

    @Autowired
    private BlueMoon.bluemoon.services.NguoiDungService nguoiDungService;
    
    @GetMapping("/resident-list") 
    public String showResidentList(Model model, Authentication auth) {
        // 1. Lấy thông tin user hiện tại
        String cccd = auth.getName();
        Optional<DoiTuong> userOpt = nguoiDungService.timBanQuanTriTheoID(cccd);
        if (userOpt.isEmpty()) {
            return "redirect:/login?error=notfound";
        }
        
        // 2. Thêm user vào model
        model.addAttribute("user", userOpt.get());
        
        // 3. Lấy và thêm danh sách cư dân
        List<DoiTuong> residentList = cuDanService.layDanhSachCuDan();
        model.addAttribute("residents", residentList);
        
        return "residents";
    }
}
