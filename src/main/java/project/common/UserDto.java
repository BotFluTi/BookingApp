package project.common;

import project.entities.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String password;

    public UserDto(Long id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
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

    public static List<UserDto> copyUsersToDto(List<User> userList) {
        return userList.stream()
                .map(user -> new UserDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getPassword()
                ))
                .collect(Collectors.toList());
    }
}

