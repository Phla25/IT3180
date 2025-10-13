package BlueMoon.bluemoon.configs;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Import các Custom UserDetailsService
import BlueMoon.bluemoon.services.CustomAccountantDetailsService;
import BlueMoon.bluemoon.services.CustomManagerDetailsService;
import BlueMoon.bluemoon.services.CustomNormalUserDetailsService;
import BlueMoon.bluemoon.services.CustomOfficerDetailsService;
import BlueMoon.bluemoon.utils.UserRole; // Import Enum Vai trò

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    // 1. Khai báo các Custom UserDetailsService (được Spring tự động inject)
    private final CustomManagerDetailsService managerService;
    private final CustomAccountantDetailsService accountantService;
    private final CustomNormalUserDetailsService normalUserService;
    private final CustomOfficerDetailsService officerService;
    
    // Constructor Injection
    public WebSecurityConfig(
        CustomManagerDetailsService managerService,
        CustomAccountantDetailsService accountantService,
        CustomNormalUserDetailsService normalUserService,
        CustomOfficerDetailsService officerService) {
        
        this.managerService = managerService;
        this.accountantService = accountantService;
        this.normalUserService = normalUserService;
        this.officerService = officerService;
    }

    // Bean để mã hóa mật khẩu (BẮT BUỘC cho Spring Security 6+)
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Luôn sử dụng Bcrypt cho mật khẩu
        return new BCryptPasswordEncoder(); 
    }
    
    // Hàm tiện ích tạo DaoAuthenticationProvider
    private DaoAuthenticationProvider createDaoProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        // Cho phép provider không ném lỗi nếu không tìm thấy người dùng (để ProviderManager thử provider tiếp theo)
        provider.setHideUserNotFoundExceptions(false); 
        return provider;
    }

    /**
     * 2. Cấu hình AuthenticationManager để xử lý xác thực bằng nhiều UserDetailsService
     * ProviderManager sẽ thử xác thực với từng provider theo thứ tự được liệt kê.
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider managerProvider = createDaoProvider(managerService);
        DaoAuthenticationProvider accountantProvider = createDaoProvider(accountantService);
        DaoAuthenticationProvider normalUserProvider = createDaoProvider(normalUserService);
        DaoAuthenticationProvider officerProvider = createDaoProvider(officerService);
        
        // Đặt thứ tự ưu tiên: Quản trị viên -> Kế toán -> Cơ quan -> Người dùng thường
        List<AuthenticationProvider> providers = Arrays.<AuthenticationProvider>asList(
                managerProvider, 
                accountantProvider, 
                officerProvider,
                normalUserProvider
        );
        
        return new ProviderManager(providers);
    }

    /**
     * 3. Cấu hình Phân quyền (Authorization) và quy tắc bảo mật HTTP
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Cấu hình Session (quan trọng nếu dùng JWT, nếu không thì dùng Session)
            // .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
            .csrf(csrf -> csrf.disable()) // Tắt CSRF (thường làm với REST API)
            .authorizeHttpRequests(auth -> auth
                
                // Vai trò cao nhất: BAN_QUAN_TRI
                .requestMatchers("/api/admin/**", "/api/taisan/**", "/api/user/**").hasRole(UserRole.BAN_QUAN_TRI.name())

                // Vai trò KẾ TOÁN (có thể truy cập Hóa đơn và Báo cáo)
                .requestMatchers("/api/hoadon/**", "/api/accounting/**").hasAnyRole(UserRole.KE_TOAN.name(), UserRole.BAN_QUAN_TRI.name())

                // Vai trò CƠ QUAN CHỨC NĂNG (chỉ được xem thông tin cư dân và thông báo)
                .requestMatchers("/api/officer/view/**").hasAnyRole(UserRole.CO_QUAN_CHUC_NANG.name(), UserRole.BAN_QUAN_TRI.name())

                // Vai trò NGƯỜI DÙNG THƯỜNG (Cư dân)
                .requestMatchers("/api/dichvu/register", "/api/incident/report", "/api/citizen/me").hasRole(UserRole.NGUOI_DUNG_THUONG.name())

                // Các API công khai (đăng ký, đăng nhập)
                .requestMatchers("/api/public/**", "/login").permitAll()

                // Yêu cầu xác thực cho tất cả các request khác còn lại
                .anyRequest().authenticated()
            )
            // Cấu hình form login cơ bản
            .formLogin(form -> form
                .defaultSuccessUrl("/home", true) // Trang sau khi đăng nhập thành công
                .permitAll()
            )
            // Cấu hình logout
            .logout(logout -> logout
                .permitAll()
            );

        return http.build();
    }
}