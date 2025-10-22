package BlueMoon.bluemoon.services;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import BlueMoon.bluemoon.daos.DoiTuongDAO;
import BlueMoon.bluemoon.entities.DoiTuong;
import BlueMoon.bluemoon.utils.AccountStatus;
import BlueMoon.bluemoon.utils.UserRole;

// Thay đổi tên class cho mỗi file: CustomManagerDetailsService, CustomAccountantDetailsService, v.v.
// @Service (Chỉ cần 1 trong 4 file có @Service nếu bạn dùng @Bean trong SecurityConfig)
@Service
public class CustomManagerDetailsService implements UserDetailsService {

    private final DoiTuongDAO doiTuongDAO;
    
    public CustomManagerDetailsService(DoiTuongDAO doiTuongDAO) {
        this.doiTuongDAO = doiTuongDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        // 1. Lấy vai trò cụ thể cho Service này
        final UserRole REQUIRED_ROLE = UserRole.ban_quan_tri; // <--- THAY ĐỔI VAI TRÒ Ở ĐÂY

        // 2. Tìm Entity (tìm bằng CCCD trước, sau đó là Email)
        DoiTuong user = doiTuongDAO.findByCccd(username)
            .orElseGet(() -> doiTuongDAO.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản " + username + " không tồn tại.")));

       // 3. KIỂM TRA VAI TRÒ PHÙ HỢP
        if (user.getVaiTro() != REQUIRED_ROLE) {
            // SỬA ĐỔI: Ném BadCredentialsException để ProviderManager dừng lại và trả về lỗi
            throw new UsernameNotFoundException("Tài khoản không phải là Ban quản trị."); // <--- Thay đổi ở đây
        }
        
        // 4. KIỂM TRA TRẠNG THÁI
        if (user.getTrangThaiTaiKhoan() != AccountStatus.hoat_dong) {
             throw new UsernameNotFoundException("Tài khoản đã bị khóa hoặc không hoạt động."); // <--- Nên dùng BadCredentialsException hoặc DisabledException
        }
        
        // 5. Trả về đối tượng UserDetails của Spring Security
        List<GrantedAuthority> authorities = Collections.singletonList(
             new SimpleGrantedAuthority("ROLE_"+REQUIRED_ROLE.name())
        );

        return new org.springframework.security.core.userdetails.User(
            user.getCccd(), 
            user.getMatKhau(), // Đảm bảo đây là mật khẩu đã BĂM (hashed)
            authorities         
        );
    }
}