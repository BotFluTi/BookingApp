package project.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import project.service.UserService;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String next, Model model) {
        model.addAttribute("showRegister", false);
        model.addAttribute("next", next);
        return "auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse res) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true).secure(false).sameSite("Lax").path("/").maxAge(0).build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return "redirect:/login?logout=true";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam String username,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  Model model) {
        if (userService.usernameExists(username)) {
            model.addAttribute("registerErrorMessage", "Username already exists.");
            model.addAttribute("showRegister", true);
            return "auth/login";
        }
        if (userService.emailExists(email)) {
            model.addAttribute("registerErrorMessage", "Email already registered.");
            model.addAttribute("showRegister", true);
            return "auth/login";
        }
        if (password.length() < 6) {
            model.addAttribute("registerErrorMessage", "Password must be at least 6 characters.");
            model.addAttribute("showRegister", true);
            return "auth/login";
        }
        try {
            userService.createUser(username, email, password);
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("registerErrorMessage", "Registration failed: " + e.getMessage());
            model.addAttribute("showRegister", true);
            return "auth/login";
        }
    }
}
