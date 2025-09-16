package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.UserDTO;
import com.example.demo.service.JWTService;
import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @PostMapping("register")
    public User register(@RequestBody UserDTO user) {
       return userService.saveUser(new User(user));
    }

    @PostMapping("login")
    public String login(@RequestBody UserDTO user) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));

        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(user.getUserName());
        } else {
            return "fail";
        }
    }
}
