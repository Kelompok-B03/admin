package id.ac.ui.cs.gatherlove.admin.service;

import id.ac.ui.cs.gatherlove.admin.dto.UserDTO;
import java.util.List;
import java.util.UUID;

public interface AuthService {
    Long getTotalUsers();
    void blockUser(UUID userId, String reason);
    List<UserDTO> getAllUsers();

    LoginResponseDTO login(LoginRequestDTO request);
    boolean isAdmin(String token);
    UserDTO getUserFromToken(String token);
}