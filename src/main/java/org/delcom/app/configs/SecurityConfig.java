package org.delcom.app.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    res.sendRedirect("/auth/login");
                }))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/assets/**", "/api/**", "/css/**", "/js/**", "/watchlists/cover/**")
                .permitAll()
                .anyRequest().authenticated())
            .formLogin(form -> form.disable())
            .logout(logout -> logout
                .logoutSuccessUrl("/auth/login")
                .deleteCookies("JSESSIONID", "remember-me") // Hapus cookie saat logout
                .permitAll())
            // --- PERBAIKAN DI SINI ---
            .rememberMe(remember -> remember
                .key("uniqueAndSecret")
                .tokenValiditySeconds(604800) // 7 Hari (86400 * 7)
                .rememberMeParameter("remember-me") // Harus sama dengan name di HTML
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}