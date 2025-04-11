package id.ac.ui.cs.gatherlove.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID id;
    private String username;
    private String email;
    private String fullName;
    private String role; // ADMIN, FUNDRAISER, DONOR
    private String status; // ACTIVE, INACTIVE, BLOCKED
    private LocalDateTime createdAt;
}