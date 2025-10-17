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

import BlueMoon.bluemoon.services.CustomAccountantDetailsService;
import BlueMoon.bluemoon.services.CustomManagerDetailsService;
import BlueMoon.bluemoon.services.CustomNormalUserDetailsService;
import BlueMoon.bluemoon.services.CustomOfficerDetailsService;
import BlueMoon.bluemoon.utils.UserRole;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final CustomManagerDetailsService managerService;
    private final CustomAccountantDetailsService accountantService;
    private final CustomNormalUserDetailsService normalUserService;
    private final CustomOfficerDetailsService officerService;

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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @SuppressWarnings("deprecation")
    private DaoAuthenticationProvider createDaoProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider managerProvider = createDaoProvider(managerService);
        DaoAuthenticationProvider accountantProvider = createDaoProvider(accountantService);
        DaoAuthenticationProvider normalUserProvider = createDaoProvider(normalUserService);
        DaoAuthenticationProvider officerProvider = createDaoProvider(officerService);

        List<AuthenticationProvider> providers = Arrays.asList(
                managerProvider,
                accountantProvider,
                officerProvider,
                normalUserProvider
        );

        return new ProviderManager(providers);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Cho phép truy cập tự do các tài nguyên tĩnh và trang đăng nhập
                .requestMatchers("/reset-password","/forgot-password","/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()

                // Các trang yêu cầu vai trò cụ thể
                .requestMatchers("/admin/**").hasRole(UserRole.ban_quan_tri.name())
                .requestMatchers("/accounting/**").hasAnyRole(UserRole.ke_toan.name(), UserRole.ban_quan_tri.name())
                .requestMatchers("/officer/**").hasAnyRole(UserRole.co_quan_chuc_nang.name(), UserRole.ban_quan_tri.name())
                .requestMatchers("/user/**").hasAnyRole(UserRole.nguoi_dung_thuong.name(), UserRole.ban_quan_tri.name())

                // Còn lại phải đăng nhập
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // Trang login MVC tự làm (vd: login.html)
                .loginProcessingUrl("/process-login") // Action của form login
                .defaultSuccessUrl("/home", true) // Trang chuyển đến sau khi login thành công
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );

        return http.build();
    }
}
