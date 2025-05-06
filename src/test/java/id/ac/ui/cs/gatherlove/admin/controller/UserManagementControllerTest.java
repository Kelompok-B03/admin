package id.ac.ui.cs.gatherlove.admin.controller;

import id.ac.ui.cs.gatherlove.admin.dto.UserDTO;
import id.ac.ui.cs.gatherlove.admin.facade.AdminFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserManagementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AdminFacade adminFacade;

    @InjectMocks
    private UserManagementController userManagementController;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userManagementController).build();
    }

    @Test
    void getAllUsers_ShouldReturnUsersList() throws Exception {
        List<UserDTO> users = Arrays.asList(
                new UserDTO(UUID.randomUUID(), "user1", "user1@example.com", "User One", "FUNDRAISER", "ACTIVE", LocalDateTime.now().minusMonths(3)),
                new UserDTO(UUID.randomUUID(), "user2", "user2@example.com", "User Two", "DONOR", "ACTIVE", LocalDateTime.now().minusMonths(2))
        );

        when(adminFacade.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    void getUserDetails_ShouldReturnUserDetails() throws Exception {
        UserDTO user = new UserDTO(
                userId, "user1", "user1@example.com", "User One",
                "FUNDRAISER", "ACTIVE", LocalDateTime.now().minusMonths(3)
        );

        when(adminFacade.getUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/api/admin/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.role").value("FUNDRAISER"));
    }

    @Test
    void getUserDetails_UserNotFound_ShouldReturnNotFound() throws Exception {
        when(adminFacade.getUserById(userId)).thenReturn(null);

        mockMvc.perform(get("/api/admin/users/" + userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void blockUser_ShouldReturnOk() throws Exception {
        String violationReason = "Melanggar aturan platform dengan menyalahgunakan dana kampanye";

        mockMvc.perform(post("/api/admin/users/" + userId + "/block")
                        .param("reason", violationReason)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());

        verify(adminFacade, times(1)).blockUser(userId, violationReason);
    }
}