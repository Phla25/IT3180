package BlueMoon.bluemoon.controllers;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import BlueMoon.bluemoon.entities.DoiTuong;


@Controller
public class LoginController {
    
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                          @RequestParam(required = false) String logout,
                          Model model) {
        if (error != null) {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng");
        }
        
        if (logout != null) {
            model.addAttribute("message", "Bạn đã đăng xuất thành công");
        }
        
        return "login";
    }

    @GetMapping("/")
    public String defaultPage(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            DoiTuong user = (DoiTuong) auth.getPrincipal();
            // Redirect based on role
            return switch (user.getVaiTro()) {
                case ban_quan_tri -> "redirect:/admin/dashboard"; 
                case co_quan_chuc_nang -> "redirect:/staff/dashboard"; 
                case ke_toan -> "redirect:/accountant/dashboard";
                default -> "redirect:/resident/dashboard";
            };
        }
        return "redirect:/login";
    }

}