package com.authservice.controllers;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.authservice.dto.JwtRequest;
import com.authservice.dto.JwtResponse;
import com.authservice.dto.UserDTO;
import com.authservice.models.Token;
import com.authservice.models.TokenType;
import com.authservice.models.User;
import com.authservice.repository.TokenRepository;
import com.authservice.repository.UserRepository;
import com.authservice.security.JwtTokenProvider;
import com.authservice.services.UserDetailsServiceImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Key;

@RestController
@CrossOrigin
public class JwtAuthController {

    /**
     * * ===========================================================================
     * * ======================== Module : JWTAuthController =======================
     * * ======================== Created By : Umesh Kumar =========================
     * * ======================== Created On : 04-06-2024 ==========================
     * * ===========================================================================
     * * | Code Status : On
     */

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TokenRepository tokenRepo;

    /**
     * * Register a new user
     * @param req
     * @return
     * @throws Exception
     */
    @PostMapping("/register")
    public UserDetails saveUser(@RequestBody UserDTO req) throws Exception {

        User user = User.builder()
                .fullname(req.getFullname())
                .email(req.getEmail())
                .password(req.getPassword())
                .role(req.getRole())
                .phoneNo(req.getPhoneNo())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userDetailsService.save(user);

    }

    /**
     * * For Testing Purposes only
     * 
     * @param req
     * @return
     */
    @GetMapping("/register")
    public UserDTO test(@RequestBody UserDTO req) {
        User user = User.builder()
                .fullname(req.getFullname())
                .email(req.getEmail())
                .password(req.getPassword())
                .role(req.getRole())
                .phoneNo(req.getPhoneNo())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return req;
    }

    /**
     * * User Login
     * 
     * @param authenticationRequest
     * @return
     * @throws Exception
     */
    @PostMapping("/login")
    public JwtResponse createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

        authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getEmail());

        final String token = jwtUtil.generateToken(userDetails);
        User user = userRepo.findByEmail(userDetails.getUsername());
        revokeAllUserTokens(user);
        saveUserToken(user, token);
        System.out.println("\n\nUser Email : " + userDetails.getUsername());
        return new JwtResponse(token);
    }

    /**
     * Authenticate the user by email & password { currently not using}
     * 
     * @param email
     * @param password
     * @throws Exception
     */
    private void authenticate(String email, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (Exception e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    /**
     * Save the user's token in database
     * 
     * @param user
     * @param jwtToken
     */
    private void saveUserToken(User user, String jwtToken) {

        var token = Token.builder()
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        tokenRepo.save(token);
    }

    /**
     * revoked all user's token (if token already available set isExpired = true)
     * 
     * @param user
     */
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepo.findAllValidTokensByUser(user.getId());

        if (validUserTokens.isEmpty())
            return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepo.saveAll(validUserTokens);

    }

    @Value("${spring.app.jwtSecret}")
    private String secretKey;

    @GetMapping("/check-validity")
    public boolean checkTokenValidity(HttpServletRequest request, HttpServletResponse response) {

        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
        }

        if (jwt == null) {
            return false;
        }

        // Parse the token to extract the claims
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        Claims claims;
        try {
            claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
        } catch (Exception e) {
            return false; // Token is invalid
        }

        // Check if the token is expired
        Date expiration = claims.getExpiration();
        if (expiration.before(new Date())) {
            // Mark the token as expired and revoked in the database
            var validUserTokens = tokenRepo.findByToken(jwt);
            if (validUserTokens.isPresent()) {
                Token token = validUserTokens.get();
                token.setExpired(true);
                token.setRevoked(true);
                tokenRepo.save(token);
            }
            return false; // Token is expired
        }

        // Check if the token is valid in the database
        var validUserTokens = tokenRepo.findByToken(jwt);
        if (validUserTokens.isPresent() && !validUserTokens.get().getExpired()
                && !validUserTokens.get().getRevoked()) {
            return true; // Token is valid
        }

        return false; // Token is invalid or not found in the database
    }
}
