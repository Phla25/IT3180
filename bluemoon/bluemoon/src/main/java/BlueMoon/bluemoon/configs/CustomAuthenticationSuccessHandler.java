package BlueMoon.bluemoon.configs;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        Authentication authentication) 
                                        throws IOException, ServletException {
        
        // Lấy danh sách các vai trò (roles) của người dùng
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String redirectUrl = "/"; // Mặc định là trang chủ

        // Duyệt qua các vai trò để xác định trang chuyển hướng
        for (GrantedAuthority grantedAuthority : authorities) {
            String role = grantedAuthority.getAuthority().toLowerCase(); // Lấy tên Role (VD: ROLE_BAN_QUAN_TRI)
            
            // Tên Role trong Spring Security mặc định có tiền tố "ROLE_"
            if (role.contains("ban_quan_tri")) {
                redirectUrl = "/admin/dashboard";
                break;
            } else if (role.contains("ke_toan")) {
                redirectUrl = "/accountant/dashboard";
                break;
            } else if (role.contains("co_quan_chuc_nang")) {
                redirectUrl = "/staff/dashboard";
                break;
            } else if (role.contains("nguoi_dung_thuong")) {
                redirectUrl = "/resident/dashboard";
                break;
            }
        }
        
        // Chuyển hướng
        response.sendRedirect(redirectUrl);
    }
}