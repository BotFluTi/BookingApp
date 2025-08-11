package project.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import project.entities.User;
import project.repository.UserRepository;
import project.service.PasswordService;
import project.service.UserService;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordService passwordService;

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String next, Model model) {
        model.addAttribute("showRegister", false);
        model.addAttribute("next", next);
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam(required = false) String next,
                               HttpSession session,
                               Model model) {
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || !passwordService.matches(password, user.getPassword())) {
            model.addAttribute("error", "Invalid username or password");
            model.addAttribute("next", next);
            return "auth/login";
        }

        session.setAttribute("loggedInUser", user);

        String dest = (next != null && next.startsWith("/")) ? next : "/";
        return "redirect:" + dest;
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
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
