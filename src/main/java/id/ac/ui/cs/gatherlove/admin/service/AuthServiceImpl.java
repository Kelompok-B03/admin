package id.ac.ui.cs.gatherlove.admin.service;

import id.ac.ui.cs.gatherlove.admin.dto.LoginRequestDTO;
import id.ac.ui.cs.gatherlove.admin.dto.LoginResponseDTO;
import id.ac.ui.cs.gatherlove.admin.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.core.Authentication;

@Service
@Primary
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final RestTemplate restTemplate;
    private final String authServiceUrl;

    public AuthServiceImpl(RestTemplate restTemplate, 
                          @Value("${auth.service.url}") String authServiceUrl) {
        this.restTemplate = restTemplate;
        this.authServiceUrl = authServiceUrl;
    }

    @Override
    public Long getTotalUsers() {
        return null;
    }

    @Override
    public void blockUser(UUID userId, String reason) {
        return null;
    }
    
    @Override
    public void unblockUser(UUID userId) {
        return null;
    }
    
    @Override
    public List<UserDTO> getAllUsers() {
        return null;
    }
    
    @Override
    public UserDTO getUserById(UUID userId) {
        return null;
    }
    
    public UserDTO getUserByEmail(String email) {
        return null;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        return null;
    }

    @Override
    public boolean isAdmin(String token) {
        return false;
    }

    @Override
    public UserDTO getUserFromToken(String token) {
        return null;
    }
}