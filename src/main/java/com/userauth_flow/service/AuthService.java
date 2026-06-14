package com.userauth_flow.service;

import com.userauth_flow.dto.request.LoginRequest;
import com.userauth_flow.dto.request.RegisterRequest;
import com.userauth_flow.dto.response.AuthResponse;
import com.userauth_flow.entity.User;
import com.userauth_flow.filter.JWTfilter;
import com.userauth_flow.repository.UserRepository;
import com.userauth_flow.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse registerUser(RegisterRequest request){

        log.info("Registering a new user with email: {}", request.getEmail());
      if(userRepository.existsByEmail(request.getEmail())){
          log.warn("User Email already exists: {}", request.getEmail());
          throw new RuntimeException("User already exists");
      }
      User user = User.builder()
              .firstName(request.getFirstName())
              .lastName(request.getLastName())
              .email(request.getEmail())
              .password(request.getPassword())
              .role(request.getRole())
              .build();


      User savedUser = userRepository.save(user);

      log.info("User saved successfully with email : {}", request.getEmail());

      String token = jwtUtil.generateToken(savedUser);
      return buildAuthResponse(savedUser,token);

    }

    private AuthResponse buildAuthResponse(User user, String jwtToken) {
        return AuthResponse.builder()
                .id(user.getId())
                .token(jwtToken)
                .expiration(jwtUtil.getExpirationTime())
                .tokenType("Bearer")
                .lName(user.getLastName())
                .build();
    }

    public String loginUser(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(RuntimeException::new);

        if(user != null && user.getPassword().equals(request.getPassword())){
            return "logged in successfully";
        }
        return null;
    }
}
