package BlueMoon.bluemoon.controllers;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


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
            String role = auth.getAuthorities().stream()
                                  .map(GrantedAuthority::getAuthority)
                                  .findFirst()
                                  .orElse(""); // Mặc định nếu không có role nà
            // Redirect based on role
            return switch (role.toLowerCase()) {
                case "ban_quan_tri" -> "redirect:/admin/dashboard"; 
                case "co_quan_chuc_nang" -> "redirect:/staff/dashboard"; 
                case "ke_toan" -> "redirect:/accountant/dashboard";
                default -> "redirect:/resident/dashboard";
            };
        }
        return "redirect:/login";
    }

}