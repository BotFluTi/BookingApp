package project;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.matches("admin", "$2a$10$011kduk2F9yYGoiny9fOWeSkH.B5UFyF/YztK8VZETda7Ej6ETjq6"));


    }
}
