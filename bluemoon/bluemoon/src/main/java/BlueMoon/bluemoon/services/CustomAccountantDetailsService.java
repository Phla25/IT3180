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

@Service
public class CustomAccountantDetailsService implements UserDetailsService {

    private final DoiTuongDAO doiTuongDAO;
    
    public CustomAccountantDetailsService(DoiTuongDAO doiTuongDAO) {
        this.doiTuongDAO = doiTuongDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        final UserRole REQUIRED_ROLE = UserRole.ke_toan;

        DoiTuong user = doiTuongDAO.findByCccd(username)
            .orElseGet(() -> doiTuongDAO.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản Kế toán " + username + " không tồn tại.")));

        // 1. KIỂM TRA VAI TRÒ
        if (user.getVaiTro() != REQUIRED_ROLE) {
            // Ném lỗi để ProviderManager chuyển sang Provider tiếp theo
            throw new UsernameNotFoundException("Tài khoản này không phải là Kế toán."); 
        }
        
        // 2. KIỂM TRA TRẠNG THÁI
        if (user.getTrangThaiTaiKhoan() != AccountStatus.hoat_dong) {
             throw new UsernameNotFoundException("Tài khoản Kế toán đã bị khóa hoặc không hoạt động.");
        }
        
        // 3. Xây dựng Authorities và trả về UserDetails
        List<GrantedAuthority> authorities = Collections.singletonList(
             new SimpleGrantedAuthority("ROLE_" + REQUIRED_ROLE.name())
        );

        return new org.springframework.security.core.userdetails.User(
            user.getCccd(), 
            user.getMatKhau(), 
            authorities         
        );
    }
}