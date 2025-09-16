package project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService uds;

    public SecurityConfig(JwtAuthFilter f, UserDetailsService uds) {
        this.jwtAuthFilter = f; this.uds = uds;
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain actuatorChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/actuator/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/", "/login", "/logout", "/about", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/rooms", "/rooms/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/rooms/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/register").permitAll()
                        .requestMatchers("/register/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(Customizer.withDefaults())
                .logout(l -> l
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .addLogoutHandler((req, res, auth) -> {
                            org.springframework.security.core.context.SecurityContextHolder.clearContext();
                            var cookie = org.springframework.http.ResponseCookie.from("jwt", "")
                                    .path("/")
                                    .httpOnly(true)
                                    .maxAge(0)
                                    .build();
                            res.addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, cookie.toString());
                        })
                );
        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://localhost:8080"));
        c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        c.setAllowedHeaders(List.of("Authorization","Content-Type"));
        UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/**", c);
        return s;
    }
}
