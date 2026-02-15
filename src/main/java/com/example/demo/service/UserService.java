package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.UserDTO;
import com.example.demo.repository.UserRepository;
import com.example.demo.utility.JWTHelper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final JWTHelper jwtHelper;
    private static final String ADMIN_NAME = "ADMIN_QUARTZ";
    private static final String ADMIN_PASSWORD = "PASSWORD";
    private static final String ADMIN_ROLE = "ROLE_ADMIN";
    public static final UserDTO userDTO = new UserDTO(ADMIN_NAME, ADMIN_PASSWORD, ADMIN_ROLE);

    @Autowired
    public UserService(JWTHelper jwtHelper, UserRepository userRepository) {
        this.jwtHelper = jwtHelper;
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public boolean isAdmin(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(value -> value.getRole().contains("ROLE_ADMIN")).orElse(false);
    }

    public Long getOwnerId(String authToken) {
        String jwtTokenValue = jwtHelper.getJwtTokenValue(authToken);
        String username = jwtHelper.extractUserName(jwtTokenValue);
        return userRepository.findByUserName(username).getId();
    }

    public Long getOwnerId(UserDTO userDTO) {
        return userRepository.findByUserName(userDTO.getUserName()).getId();
    }

    @PostConstruct
    public void init() {
        this.saveUser(new User(userDTO));
    }
}
