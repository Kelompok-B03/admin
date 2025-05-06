package id.ac.ui.cs.gatherlove.admin.controller;

import id.ac.ui.cs.gatherlove.admin.dto.UserDTO;
import id.ac.ui.cs.gatherlove.admin.facade.AdminFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "*") // Sesuaikan dengan kebutuhan keamanan
public class UserManagementController {

    private final AdminFacade adminFacade;

    @Autowired
    public UserManagementController(AdminFacade adminFacade) {
        this.adminFacade = adminFacade;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = adminFacade.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserDetails(@PathVariable UUID userId) {
        UserDTO user = adminFacade.getUserById(userId);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }

    @PostMapping("/{userId}/block")
    public ResponseEntity<Void> blockUser(
            @PathVariable UUID userId,
            @RequestParam String reason) {

        adminFacade.blockUser(userId, reason);
        return ResponseEntity.ok().build();
    }
}