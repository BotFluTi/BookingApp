package project.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import project.service.JwtService;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService uds;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService uds) {
        this.jwtService = jwtService; this.uds = uds;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String auth = req.getHeader("Authorization");
        String token = null;

        if (auth != null && auth.startsWith("Bearer ")) {
            token = auth.substring(7);
        }
        if (token == null && req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("jwt".equals(c.getName())) {
                    token = c.getValue();
                    break;
                }
            }
        }

        if (token != null) {
            try {
                String username = jwtService.extractUsername(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails user = uds.loadUserByUsername(username);
                    if (jwtService.isValid(token, user)) {
                        UsernamePasswordAuthenticationToken t =
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        t.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                        SecurityContextHolder.getContext().setAuthentication(t);
                    }
                }
            } catch (Exception ignored) {}
        }
        chain.doFilter(req, res);
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String p = request.getRequestURI();
        return p.startsWith("/actuator/") || p.startsWith("/auth/") || p.startsWith("/login") || p.startsWith("/logout") || p.startsWith("/css/") || p.startsWith("/js/") || p.startsWith("/images/");
    }
}
