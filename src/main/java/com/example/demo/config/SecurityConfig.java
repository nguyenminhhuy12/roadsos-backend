package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
            	.requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()	
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/technician-application/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/categories").permitAll()
                .requestMatchers("/api/categories/**").hasRole("ADMIN")
                .requestMatchers("/api/statistics/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Component
    public static class JwtFilter extends OncePerRequestFilter {

        @Autowired
        private JwtUtil jwtUtil;

        @Autowired
        private UserRepository userRepository;

        @Override
        protected void doFilterInternal(HttpServletRequest request,
						                HttpServletResponse response,
						                FilterChain chain)
				throws ServletException, IOException {
				
			String header = request.getHeader("Authorization");
			System.out.println("Header: " + header);
			System.out.println("URI: " + request.getRequestURI());
			
			if (header != null && header.startsWith("Bearer ")) {
				String token = header.replace("Bearer ", "");
			try {
				String phone = jwtUtil.extractPhone(token);
				System.out.println("Phone: " + phone);
				User user = userRepository.findByPhone(phone).orElse(null);
				System.out.println("User: " + (user != null ? user.getRole() : "null"));
			
			if (user != null) {
				String role = "ROLE_" + user.getRole().name();
				UsernamePasswordAuthenticationToken auth =
				    new UsernamePasswordAuthenticationToken(
				        phone,
				        null,
				        List.of(new SimpleGrantedAuthority(role))
				    );
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		} catch (Exception e) {
			System.out.println("Lỗi token: " + e.getMessage());
		}
			}
			
			chain.doFilter(request, response);
			}
    }
}