package project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.common.UserDto;
import project.entities.User;

import project.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordService passwordService;

    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserDto(u.getId(), u.getUsername(), u.getEmail(), u.getPassword(), u.getRole().name()))
                .collect(Collectors.toList());
    }



    public void createUser(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordService.encode(password));

        user.setRole(User.Role.USER);

        userRepository.save(user);
    }


    public void changeUserRole(String username, String newRoleStr) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User.Role newRole = User.Role.valueOf(newRoleStr.toUpperCase());

        user.setRole(newRole);
        userRepository.save(user);
    }


    public UserDto findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(u -> new UserDto(u.getId(), u.getUsername(), u.getEmail(), u.getPassword(), u.getRole().name()))
                .orElseThrow(() -> new RuntimeException("User with ID " + id + " not found"));
    }

    public void updateUser(Long id, String username, String email, String password, String roleStr) {
        Optional<User> optional = userRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("User with ID " + id + " not found");
        }
        User user = optional.get();
        user.setUsername(username);
        user.setEmail(email);

        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(passwordService.encode(password));
        }

        if (roleStr != null && !roleStr.trim().isEmpty()) {
            user.setRole(User.Role.valueOf(roleStr.toUpperCase()));
        }

        userRepository.save(user);
    }

    public void deleteUsersByIds(Collection<Long> ids) {
        for (Long id : ids) {
            userRepository.findById(id).ifPresent(userRepository::delete);
        }
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

}


