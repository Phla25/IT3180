package BlueMoon.bluemoon.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import BlueMoon.bluemoon.services.NguoiDungService;

@Controller
public class ResetPasswordController {

    @Autowired
    private NguoiDungService nguoiDungService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "password/forgot";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, 
                                      RedirectAttributes redirectAttributes) {
        try {
            nguoiDungService.taoYeuCauResetMatKhau(email);
            redirectAttributes.addFlashAttribute("message", 
                "Link đặt lại mật khẩu đã được gửi đến email của bạn.");
            return "redirect:/forgot-password";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "password/reset";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                     @RequestParam("password") String password,
                                     RedirectAttributes redirectAttributes) {
        try {
            nguoiDungService.resetMatKhau(token, password);
            redirectAttributes.addFlashAttribute("message", "Mật khẩu đã được đặt lại thành công!");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reset-password?token=" + token;
        }
    }
}