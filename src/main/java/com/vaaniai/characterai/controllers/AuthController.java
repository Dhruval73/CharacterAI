package com.vaaniai.characterai.controllers;

import com.vaaniai.characterai.DTO.AuthRequest;
import com.vaaniai.characterai.DTO.AuthResponse;
import com.vaaniai.characterai.model.User;
import com.vaaniai.characterai.repository.UserRepository;
import com.vaaniai.characterai.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final String token = jwtUtil.generateToken(userDetails);
        Optional<User> userOpt = userRepository.findByEmail(authRequest.getEmail());

        int loginCount = 0;

        if(userOpt.isPresent()) {

            userOpt.get().setLoginCount(userOpt.get().getLoginCount() + 1);
            loginCount = userOpt.get().getLoginCount();
            userRepository.save(userOpt.get());
        }

        System.out.println(loginCount);

        return ResponseEntity.ok(new AuthResponse(token,loginCount));
    }
}
