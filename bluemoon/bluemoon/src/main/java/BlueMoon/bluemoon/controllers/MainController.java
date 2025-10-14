package BlueMoon.bluemoon.controllers;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import BlueMoon.bluemoon.entities.DoiTuong;
import BlueMoon.bluemoon.models.UserResponse;


@Controller
public class MainController {
	 // Ánh xạ đến file login.html
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Trả về tên file template (login.html)
    }

    // Trang chủ sau khi đăng nhập thành công
    // URL này được bảo vệ bởi Spring Security
    @GetMapping("/home")
    public String homePage() {
        return "home"; // Trả về file home.html
    }
    
    @GetMapping("/profile")
    public String profile(Model model, Authentication auth) {
        DoiTuong user = (DoiTuong) auth.getPrincipal();
        model.addAttribute("user", new UserResponse(user));
        return "profile";
    }

}
