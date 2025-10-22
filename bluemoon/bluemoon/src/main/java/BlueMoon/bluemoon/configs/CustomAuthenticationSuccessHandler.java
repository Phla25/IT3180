package BlueMoon.bluemoon.configs;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

        String roleFromForm = request.getParameter("user_role");

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isUser       = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_nguoi_dung_thuong"));
        boolean isAdmin      = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ban_quan_tri"));
        boolean isOfficer    = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_co_quan_chuc_nang"));
        boolean isAccountant = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ke_toan"));

        // Chặn sai vai trò từ form
        if (roleFromForm.equals("nguoi_dung_thuong") && !isUser) {
            blockWrongRole(request, response, "/login?error=sai+vai+tro");
            return;
        }
        if (roleFromForm.equals("ban_quan_tri") && !isAdmin) {
            blockWrongRole(request, response, "/login?error=sai+vai+tro");
            return;
        }
        if (roleFromForm.equals("co_quan_chuc_nang") && !isOfficer) {
            blockWrongRole(request, response, "/login?error=sai+vai+tro");
            return;
        }
        if (roleFromForm.equals("ke_toan") && !isAccountant) {
            blockWrongRole(request, response, "/login?error=sai+vai+tro");
            return;
        }

        // Đúng role → điều hướng đến dashboard tương ứng
        if (isAdmin)        response.sendRedirect("/admin/dashboard");
        else if (isOfficer) response.sendRedirect("/officer/dashboard");
        else if (isAccountant) response.sendRedirect("/accountant/dashboard");
        else                response.sendRedirect("/resident/dashboard");
    }

    private void blockWrongRole(HttpServletRequest request, 
                                HttpServletResponse response, 
                                String redirectUrl) throws IOException {
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        response.sendRedirect(redirectUrl);
    }

}