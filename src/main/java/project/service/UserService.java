package project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.common.UserDto;
import project.entities.User;
import project.entities.UserGroup;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager entityManager;

    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserDto(u.getId(), u.getUsername(), u.getEmail(), u.getPassword()))
                .collect(Collectors.toList());
    }

    public void createUser(String username, String email, String password, Collection<String> groups) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordService.encode(password));
        userRepository.save(user);

        assignGroupsToUser(username, groups);
    }


    private void assignGroupsToUser(String username, Collection<String> groups) {
        User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

        for (String groupName : groups) {
            UserGroup group = entityManager.createQuery(
                            "SELECT g FROM UserGroup g WHERE g.userGroup = :name", UserGroup.class)
                    .setParameter("name", groupName)
                    .getSingleResult();

            user.setUserGroup(group);
            userRepository.save(user);
        }
    }

    public UserDto findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(u -> new UserDto(u.getId(), u.getUsername(), u.getEmail(), u.getPassword()))
                .orElseThrow(() -> new RuntimeException("User with ID " + id + " not found"));
    }

    public void updateUser(Long id, String username, String email, String password) {
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

        userRepository.save(user);
    }

    public void deleteUsersByIds(Collection<Long> ids) {
        for (Long id : ids) {
            userRepository.findById(id).ifPresent(userRepository::delete);
        }
    }


}

