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
                          @Value("${AUTH_SERVICE_URL}") String authServiceUrl) {
        this.restTemplate = restTemplate;
        this.authServiceUrl = authServiceUrl;
    }

    @Override
    public Long getTotalUsers() {
        // Get token from SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        HttpHeaders headers = new HttpHeaders();

        if (auth != null && auth.getCredentials() != null) {
            String token = (String) auth.getCredentials();
            headers.setBearerAuth(token);
        }

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Long>> response = restTemplate.exchange(
            authServiceUrl + "/api/users/count",
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<Map<String, Long>>() {}
        );

        Map<String, Long> result = response.getBody();
        return result != null ? result.get("totalUsers") : 0L;
    }

    @Override
    public void blockUser(UUID userId, String reason) {
        // Get token from SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (auth == null || auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("User does not have ADMIN role");
        }
        if (auth != null && auth.getCredentials() != null) {
            String token = (String) auth.getCredentials();
            headers.setBearerAuth(token);
        }

        // The reason is not used in the endpoint anymore
        HttpEntity<Void> request = new HttpEntity<>(headers);

        log.debug("Making PUT request to {}", authServiceUrl + "/api/users/" + userId + "/block");
        restTemplate.exchange(
            authServiceUrl + "/api/users/" + userId + "/block",
            HttpMethod.PUT,
            request,
            Void.class
        );
    }
    
    @Override
    public void unblockUser(UUID userId) {
        // Get token from SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (auth == null || auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("User does not have ADMIN role");
        }
        if (auth != null && auth.getCredentials() != null) {
            String token = (String) auth.getCredentials();
            headers.setBearerAuth(token);
        }

        // Create request entity without body
        HttpEntity<Void> request = new HttpEntity<>(headers);

        log.debug("Making PUT request to {}", authServiceUrl + "/api/users/" + userId + "/unblock");
        restTemplate.exchange(
            authServiceUrl + "/api/users/" + userId + "/unblock",
            HttpMethod.PUT,
            request,
            Void.class
        );
    }
    
    @Override
    public List<UserDTO> getAllUsers() {
        // Ambil token dari SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        log.debug("Authentication in context: {}", auth != null ? "present" : "not present");
        log.debug("Credentials in auth: {}", auth != null && auth.getCredentials() != null ? "present" : "not present");

        if (auth == null || auth.getCredentials() == null) {
            throw new IllegalStateException("No authentication token available");
        }
        String token = (String) auth.getCredentials();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        log.debug("Making GET request to {}", authServiceUrl + "/api/users");
        ResponseEntity<List<UserDTO>> response = restTemplate.exchange(
            authServiceUrl + "/api/users",
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<List<UserDTO>>() {}
        );

        return response.getBody();
    }
    
    @Override
    public UserDTO getUserById(UUID userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        HttpHeaders headers = new HttpHeaders();

        if (auth != null && auth.getCredentials() != null) {
            String token = (String) auth.getCredentials();
            headers.setBearerAuth(token);
        }

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // Updated to use the correct path from controller
        log.debug("Making GET request to {}", authServiceUrl + "/api/users/" + userId);
        ResponseEntity<UserDTO> response = restTemplate.exchange(
            authServiceUrl + "/api/users/" + userId,
            HttpMethod.GET,
            requestEntity,
            UserDTO.class
        );

        return response.getBody();
    }
    
    public UserDTO getUserByEmail(String email) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        HttpHeaders headers = new HttpHeaders();

        if (auth != null && auth.getCredentials() != null) {
            String token = (String) auth.getCredentials();
            headers.setBearerAuth(token);
        }

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // Use the new email endpoint
        log.debug("Making GET request to {}", authServiceUrl + "/api/users/email/" + email);
        ResponseEntity<UserDTO> response = restTemplate.exchange(
            authServiceUrl + "/api/users/email/" + email,
            HttpMethod.GET,
            requestEntity,
            UserDTO.class
        );

        return response.getBody();
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginRequestDTO> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<LoginResponseDTO> response = restTemplate.exchange(
            authServiceUrl + "/auth/login",
            HttpMethod.POST,
            requestEntity,
            LoginResponseDTO.class
        );

        return response.getBody();
    }

    @Override
    public boolean isAdmin(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                authServiceUrl + "/auth/me",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> userData = response.getBody();
            if (userData != null && userData.containsKey("roles")) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) userData.get("roles");
                return roles.contains("ADMIN");
            }
            return false;
        } catch (Exception e) {
            log.error("Error checking if user is admin: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public UserDTO getUserFromToken(String token) {
        // Siapkan headers untuk authorization
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);  // Letakkan token di Authorization header
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Buat entity dengan headers (tanpa body)
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                authServiceUrl + "/auth/me",
                HttpMethod.GET,  // Ubah ke GET sesuai dengan endpoint
                requestEntity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> userData = response.getBody();
            if (userData != null) {
                UserDTO user = new UserDTO();
                user.setEmail((String) userData.get("email"));
                user.setName((String) userData.get("name"));

                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) userData.get("roles");
                // Gunakan setRoles bukan setRole untuk menyesuaikan dengan struktur UserDTO baru
                user.setRoles(roles);

                // Tambahkan id jika tersedia
                if (userData.get("id") != null) {
                    try {
                        user.setId(UUID.fromString((String) userData.get("id")));
                    } catch (IllegalArgumentException e) {
                        log.warn("Could not parse user ID from token response");
                    }
                }

                // Set field lain jika tersedia
                if (userData.get("active") != null) {
                    user.setActive((Boolean) userData.get("active"));
                }

                return user;
            }
            return null;
        } catch (Exception e) {
            log.error("Error getting user from token: {}", e.getMessage());
            return null;
        }
    }
}