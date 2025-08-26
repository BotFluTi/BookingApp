package project.config;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
public class GlobalViewAuth {

    @ModelAttribute
    public void addAuthAttributes(Model model, Authentication auth) {
        boolean authenticated = auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);

        String username = authenticated ? auth.getName() : null;
        boolean isAdmin = authenticated && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        model.addAttribute("currentUsername", username);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isAuthenticated", authenticated);
    }
}
