package BlueMoon.bluemoon.configs;

import java.io.IOException;
import java.util.Collection;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
                                        throws IOException, ServletException {

        // Lấy danh sách vai trò (role) của người dùng sau khi login
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String redirectUrl = "/home"; // Mặc định

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            // Tùy theo role điều hướng về trang tương ứng
            switch (role) {
                case "ROLE_BAN_QUAN_TRI":
                    redirectUrl = "/admin/dashboard";
                    break;
                case "ROLE_KE_TOAN":
                    redirectUrl = "/accountant/dashboard";
                    break;
                case "ROLE_CO_QUAN_CHUC_NANG":
                    redirectUrl = "/officer/dashboard";
                    break;
                case "ROLE_NGUOI_DUNG_THUONG":
                    redirectUrl = "/citizen/home";
                    break;
                default:
                    redirectUrl = "/home";
                    break;
            }
        }

        response.sendRedirect(redirectUrl);
    }
}
