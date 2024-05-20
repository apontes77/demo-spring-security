package com.alexandre.demo.controller.login;

import com.alexandre.demo.controller.login.request.LoginRequest;
import com.alexandre.demo.controller.login.response.LoginResponse;
import com.alexandre.demo.entities.Role;
import com.alexandre.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/login")
public class LoginController {


    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginController(JwtEncoder jwtEncoder,
                           UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        var userObtained = userRepository.findByUsername(request.username());

        if (userObtained.isEmpty() || !userObtained.get().isLoginIncorrect(request, passwordEncoder)) {
            throw new BadCredentialsException("user or password is invalid");
        }

        var now = Instant.now();
        var expiresIn = 300L;

        var scopes = userObtained.get().getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("demoBackend")
                .subject(userObtained.get().getUserId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope", scopes)
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new LoginResponse(jwtValue, expiresIn));

    }
}
