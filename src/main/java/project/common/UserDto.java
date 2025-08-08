package project.common;

import project.entities.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String role;  // nou

    public UserDto(Long id, String username, String email, String password, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    private String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public static List<UserDto> copyUsersToDto(List<User> userList) {
        return userList.stream()
                .map(user -> new UserDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getRole() != null ? user.getRole().name() : null
                ))
                .collect(Collectors.toList());
    }
}
