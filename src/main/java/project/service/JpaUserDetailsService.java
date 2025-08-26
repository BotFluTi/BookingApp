package project.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import project.entities.User;
import project.repository.UserRepository;

import java.util.List;

@Service
public class JpaUserDetailsService implements UserDetailsService {
    private final UserRepository repo;
    public JpaUserDetailsService(UserRepository repo) { this.repo = repo; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        String role = "ROLE_" + u.getRole().name();
        List<GrantedAuthority> auth = List.of(new SimpleGrantedAuthority(role));
        return new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPassword(), auth);
    }
}
