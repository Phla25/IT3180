package BlueMoon.bluemoon.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import BlueMoon.bluemoon.models.PasswordChangeRequest;
import BlueMoon.bluemoon.services.NguoiDungService;
import jakarta.servlet.http.HttpSession;

@Controller
public class ChangePasswordController {

    @Autowired
    private NguoiDungService nguoiDungService;

    @GetMapping("/password/change")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("passwordChangeRequest", new PasswordChangeRequest());
        return "password/change";
    }

    @PostMapping("/password/change")
    public String changePassword(
            @ModelAttribute PasswordChangeRequest passwordChangeRequest,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            // Lấy CCCD từ session
            String cccd = (String) session.getAttribute("username");
            if (cccd == null) {
                redirectAttributes.addFlashAttribute("error", "Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại!");
                return "redirect:/login";
            }

            // Validate input
            if (passwordChangeRequest.getMatKhauCu() == null || passwordChangeRequest.getMatKhauMoi() == null) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng nhập đầy đủ mật khẩu cũ và mới!");
                return "redirect:/password/change";
            }

            // Thực hiện đổi mật khẩu
            nguoiDungService.doiMatKhau(
                cccd,
                passwordChangeRequest.getMatKhauCu(), 
                passwordChangeRequest.getMatKhauMoi()
            );
        
            redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
            return "redirect:/password/change";
        } catch (IllegalArgumentException e) {
            // Xử lý lỗi validate
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/password/change";
        } catch (Exception e) {
            // Xử lý lỗi hệ thống
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại sau!");
            return "redirect:/password/change";
        }
    }   
}