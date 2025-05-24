package id.ac.ui.cs.gatherlove.admin.controller;

import id.ac.ui.cs.gatherlove.admin.dto.UserDTO;
import id.ac.ui.cs.gatherlove.admin.facade.AdminFacade;
import id.ac.ui.cs.gatherlove.admin.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    private final AdminFacade adminFacade;
    private final AuthService authService;

    @Autowired
    public UserManagementController(AdminFacade adminFacade, AuthService authService) {
        this.adminFacade = adminFacade;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return null;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserDetails(@PathVariable UUID userId) {
        return null;
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        return null;
    }

    @PutMapping("/{userId}/block")
    public ResponseEntity<?> blockUser(
            @PathVariable UUID userId,
            @RequestParam String reason) {
        return null;
    }

    @PutMapping("/{userId}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable UUID userId) {
        return null;
    }
    
    @GetMapping("/count")
    public ResponseEntity<?> getUserCount() {
        return null;
    }
}