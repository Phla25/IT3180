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
            
            // 1. Lấy vai trò (Authority) từ đối tượng Authentication (dưới dạng ROLE_xxx)
            String role = auth.getAuthorities().stream()
                                  .map(GrantedAuthority::getAuthority)
                                  .findFirst()
                                  .orElse(""); // Nếu không có vai trò, mặc định là rỗng

            // 2. PHẢI KIỂM TRA VAI TRÒ ĐẦY ĐỦ (có tiền tố ROLE_ và chuyển sang chữ thường)
            // (Đã chính xác trong file gốc, giữ nguyên)
            return switch (role.toLowerCase()) {
                case "role_ban_quan_tri" -> "redirect:/admin/dashboard"; 
                case "role_co_quan_chuc_nang" -> "redirect:/staff/dashboard"; 
                case "role_ke_toan" -> "redirect:/accountant/dashboard";
                case "role_nguoi_dung_thuong" -> "redirect:/resident/dashboard"; // Thêm trường hợp rõ ràng cho người dùng thường
                default -> "redirect:/login?error=invalidrole"; // Chuyển hướng nếu vai trò không khớp
            };
        }
        return "redirect:/login";
    }

}