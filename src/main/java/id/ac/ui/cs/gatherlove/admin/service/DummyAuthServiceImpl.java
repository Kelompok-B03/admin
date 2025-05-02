package id.ac.ui.cs.gatherlove.admin.service;

import id.ac.ui.cs.gatherlove.admin.dto.LoginRequestDTO;
import id.ac.ui.cs.gatherlove.admin.dto.LoginResponseDTO;
import id.ac.ui.cs.gatherlove.admin.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DummyAuthServiceImpl implements AuthService {

    private final List<UserDTO> dummyUsers = new ArrayList<>();
    private final Map<String, UserDTO> tokenStorage = new HashMap<>();

    public DummyAuthServiceImpl() {
        // Inisialisasi beberapa data dummy
        dummyUsers.add(new UserDTO(UUID.randomUUID(), "admin1", "admin@example.com", "Admin User", "ADMIN", "ACTIVE", LocalDateTime.now().minusMonths(6)));
        dummyUsers.add(new UserDTO(UUID.randomUUID(), "fundraiser1", "fundraiser1@example.com", "Fundraiser One", "FUNDRAISER", "ACTIVE", LocalDateTime.now().minusMonths(3)));
        dummyUsers.add(new UserDTO(UUID.randomUUID(), "fundraiser2", "fundraiser2@example.com", "Fundraiser Two", "FUNDRAISER", "ACTIVE", LocalDateTime.now().minusMonths(2)));
        dummyUsers.add(new UserDTO(UUID.randomUUID(), "donor1", "donor1@example.com", "Donor One", "DONOR", "ACTIVE", LocalDateTime.now().minusMonths(1)));
        dummyUsers.add(new UserDTO(UUID.randomUUID(), "donor2", "donor2@example.com", "Donor Two", "DONOR", "ACTIVE", LocalDateTime.now().minusWeeks(3)));
    }

    @Override
    public Long getTotalUsers() {
        return (long) dummyUsers.size();
    }

    @Override
    public void blockUser(UUID userId, String reason) {
        // Logic untuk mengubah status user menjadi BLOCKED
        dummyUsers.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .ifPresent(user -> user.setStatus("BLOCKED"));

        // Dalam implementasi sebenarnya, akan ada panggilan API ke service auth
        System.out.println("User dengan ID " + userId + " diblokir dengan alasan: " + reason);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return new ArrayList<>(dummyUsers);
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        // Dalam implementasi nyata, ini akan melakukan panggilan ke service auth
        // Untuk implementasi dummy, kita hanya memeriksa apakah username ada di dummyUsers

        Optional<UserDTO> userOptional = dummyUsers.stream()
                .filter(user -> user.getUsername().equals(request.getUsername()) && "ACTIVE".equals(user.getStatus()))
                .findFirst();

        if (userOptional.isPresent()) {
            UserDTO user = userOptional.get();
            // Buat token sederhana (dalam implementasi nyata, ini akan menggunakan JWT)
            String token = UUID.randomUUID().toString();
            tokenStorage.put(token, user);

            return LoginResponseDTO.builder()
                    .success(true)
                    .token(token)
                    .user(user)
                    .build();
        } else {
            return LoginResponseDTO.builder()
                    .success(false)
                    .errorMessage("Username atau password salah")
                    .build();
        }
    }

    @Override
    public boolean isAdmin(String token) {
        UserDTO user = tokenStorage.get(token);
        return user != null && "ADMIN".equals(user.getRole());
    }

    @Override
    public UserDTO getUserFromToken(String token) {
        return tokenStorage.get(token);
    }
}