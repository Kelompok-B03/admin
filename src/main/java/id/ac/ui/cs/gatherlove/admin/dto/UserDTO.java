package id.ac.ui.cs.gatherlove.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID id;
    private String email;
    private String name;
    private String phoneNumber;
    private String bio;
    private String profilePictureUrl;
    private Long walletId;
    private boolean active;
    private List<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Helper method to get primary role (for backward compatibility)
    public String getRole() {
        return roles != null && !roles.isEmpty() ? roles.get(0) : null;
    }
    
    // Helper method for backward compatibility
    public String getStatus() {
        return active ? "ACTIVE" : "BLOCKED";
    }
    
    // Legacy constructor for backward compatibility
    public UserDTO(UUID id, String username, String email, String name, 
                  String role, String status, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.active = "ACTIVE".equalsIgnoreCase(status);
        this.createdAt = createdAt;
        
        // Convert single role to list
        if (role != null) {
            this.roles = List.of(role);
        }
    }
}