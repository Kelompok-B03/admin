package id.ac.ui.cs.gatherlove.admin.controller;

import id.ac.ui.cs.gatherlove.admin.dto.UserDTO;
import id.ac.ui.cs.gatherlove.admin.facade.AdminFacade;
import id.ac.ui.cs.gatherlove.admin.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "*")
public class UserManagementController {

    private final AuthService authService;

    @Autowired
    public UserManagementController(AdminFacade adminFacade, AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserDTO> users = authService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getMessage(),
                            "status", e.getStatusCode().value()));
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Gagal menghubungi auth service: " + e.getMessage(),
                            "status", HttpStatus.SERVICE_UNAVAILABLE.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan: " + e.getMessage(),
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserDetails(@PathVariable UUID userId) {
        try {
            UserDTO user = authService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(user);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getMessage(),
                            "status", e.getStatusCode().value()));
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Gagal menghubungi auth service",
                            "status", HttpStatus.SERVICE_UNAVAILABLE.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan: " + e.getMessage(),
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            UserDTO user = authService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(user);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getMessage(),
                            "status", e.getStatusCode().value()));
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Gagal menghubungi auth service",
                            "status", HttpStatus.SERVICE_UNAVAILABLE.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan: " + e.getMessage(),
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PutMapping("/{userId}/block")
    public ResponseEntity<?> blockUser(
            @PathVariable UUID userId,
            @RequestParam String reason) {
        try {
            authService.blockUser(userId, reason);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User berhasil diblokir");
            response.put("userId", userId);
            response.put("reason", reason);
            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getMessage(),
                            "status", e.getStatusCode().value()));
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Gagal menghubungi auth service",
                            "status", HttpStatus.SERVICE_UNAVAILABLE.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan: " + e.getMessage(),
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PutMapping("/{userId}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable UUID userId) {
        try {
            authService.unblockUser(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User berhasil dibuka blokirannya");
            response.put("userId", userId);
            return ResponseEntity.ok(response);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getMessage(),
                            "status", e.getStatusCode().value()));
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Gagal menghubungi auth service",
                            "status", HttpStatus.SERVICE_UNAVAILABLE.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan: " + e.getMessage(),
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<?> getUserCount() {
        try {
            Long count = authService.getTotalUsers();
            return ResponseEntity.ok(Map.of("count", count));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getMessage(),
                            "status", e.getStatusCode().value()));
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Gagal menghubungi auth service",
                            "status", HttpStatus.SERVICE_UNAVAILABLE.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Terjadi kesalahan: " + e.getMessage(),
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}