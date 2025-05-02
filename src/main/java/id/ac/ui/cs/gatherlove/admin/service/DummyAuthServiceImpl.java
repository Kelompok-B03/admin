package id.ac.ui.cs.gatherlove.admin.service;

import id.ac.ui.cs.gatherlove.admin.dto.UserDTO;
import id.ac.ui.cs.gatherlove.admin.service.AuthService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DummyAuthServiceImpl implements AuthService {

    private final List<UserDTO> dummyUsers = new ArrayList<>();

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
}