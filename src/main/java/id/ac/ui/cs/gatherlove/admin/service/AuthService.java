package id.ac.ui.cs.gatherlove.admin.service;

import id.ac.ui.cs.gatherlove.admin.dto.LoginRequestDTO;
import id.ac.ui.cs.gatherlove.admin.dto.LoginResponseDTO;
import id.ac.ui.cs.gatherlove.admin.dto.UserDTO;
import java.util.List;
import java.util.UUID;

public interface AuthService {
    Long getTotalUsers();
    void blockUser(UUID userId, String reason);
    List<UserDTO> getAllUsers();
    UserDTO getUserById(UUID userId);

    LoginResponseDTO login(LoginRequestDTO request);
    boolean isAdmin(String token);
    UserDTO getUserFromToken(String token);
}