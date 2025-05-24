package id.ac.ui.cs.gatherlove.admin.controller;

import id.ac.ui.cs.gatherlove.admin.dto.UserDTO;
import id.ac.ui.cs.gatherlove.admin.facade.AdminFacade;
import id.ac.ui.cs.gatherlove.admin.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.http.HttpStatus;

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
    
    @Mock
    private AuthService authService;

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
        // Setup updated UserDTO with roles list
        List<String> adminRoles = Arrays.asList("ADMIN");
        List<String> userRoles = Arrays.asList("USER");
        
        UserDTO user1 = new UserDTO();
        user1.setId(UUID.randomUUID());
        user1.setEmail("user1@example.com");
        user1.setName("User One");
        user1.setRoles(adminRoles);
        user1.setActive(true);
        user1.setCreatedAt(LocalDateTime.now().minusMonths(3));
        
        UserDTO user2 = new UserDTO();
        user2.setId(UUID.randomUUID());
        user2.setEmail("user2@example.com");
        user2.setName("User Two");
        user2.setRoles(userRoles);
        user2.setActive(true);
        user2.setCreatedAt(LocalDateTime.now().minusMonths(2));
        
        List<UserDTO> users = Arrays.asList(user1, user2);

        when(authService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("user1@example.com"))
                .andExpect(jsonPath("$[1].email").value("user2@example.com"))
                .andExpect(jsonPath("$[0].roles[0]").value("ADMIN"))
                .andExpect(jsonPath("$[1].roles[0]").value("USER"));
                
        verify(authService, times(1)).getAllUsers();
    }

    @Test
    void getUserDetails_ShouldReturnUserDetails() throws Exception {
        UserDTO user = new UserDTO();
        user.setId(userId);
        user.setEmail("user1@example.com");
        user.setName("User One");
        user.setRoles(Arrays.asList("FUNDRAISER"));
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now().minusMonths(3));

        when(authService.getUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/api/admin/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user1@example.com"))
                .andExpect(jsonPath("$.roles[0]").value("FUNDRAISER"));
                
        verify(authService, times(1)).getUserById(userId);
    }

    @Test
    void getUserDetails_UserNotFound_ShouldReturnNotFound() throws Exception {
        when(authService.getUserById(userId)).thenReturn(null);

        mockMvc.perform(get("/api/admin/users/" + userId))
                .andExpect(status().isNotFound());
                
        verify(authService, times(1)).getUserById(userId);
    }
    
    @Test
    void getUserByEmail_ShouldReturnUserDetails() throws Exception {
        String email = "user1@example.com";
        
        UserDTO user = new UserDTO();
        user.setId(userId);
        user.setEmail(email);
        user.setName("User One");
        user.setRoles(Arrays.asList("FUNDRAISER"));
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now().minusMonths(3));

        when(authService.getUserByEmail(email)).thenReturn(user);

        mockMvc.perform(get("/api/admin/users/email/" + email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.roles[0]").value("FUNDRAISER"));
                
        verify(authService, times(1)).getUserByEmail(email);
    }
    
    @Test
    void getUserByEmail_UserNotFound_ShouldReturnNotFound() throws Exception {
        String email = "nonexistent@example.com";
        when(authService.getUserByEmail(email)).thenReturn(null);

        mockMvc.perform(get("/api/admin/users/email/" + email))
                .andExpect(status().isNotFound());
                
        verify(authService, times(1)).getUserByEmail(email);
    }

    @Test
    void blockUser_ShouldReturnOk() throws Exception {
        String violationReason = "Melanggar aturan platform";

        doNothing().when(authService).blockUser(userId, violationReason);

        mockMvc.perform(put("/api/admin/users/" + userId + "/block")
                        .param("reason", violationReason)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User berhasil diblokir"))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.reason").value(violationReason));

        verify(authService, times(1)).blockUser(userId, violationReason);
    }
    
    @Test
    void unblockUser_ShouldReturnOk() throws Exception {
        doNothing().when(authService).unblockUser(userId);

        mockMvc.perform(put("/api/admin/users/" + userId + "/unblock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User berhasil dibuka blokirannya"))
                .andExpect(jsonPath("$.userId").value(userId.toString()));

        verify(authService, times(1)).unblockUser(userId);
    }
    
    @Test
    void getUserCount_ShouldReturnCount() throws Exception {
        Long userCount = 42L;
        when(authService.getTotalUsers()).thenReturn(userCount);

        mockMvc.perform(get("/api/admin/users/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(userCount));
                
        verify(authService, times(1)).getTotalUsers();
    }
    
    @Test
    void blockUser_WhenServiceThrowsException_ShouldReturnError() throws Exception {
        String violationReason = "Melanggar aturan platform";
        
        doThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN, "Access denied"))
            .when(authService).blockUser(userId, violationReason);

        mockMvc.perform(put("/api/admin/users/" + userId + "/block")
                        .param("reason", violationReason)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("403 Access denied"))
                .andExpect(jsonPath("$.status").value(403));

        verify(authService, times(1)).blockUser(userId, violationReason);
    }
    
    @Test
    void unblockUser_WhenServiceUnavailable_ShouldReturnServiceUnavailable() throws Exception {
        doThrow(new RestClientException("Connection refused"))
            .when(authService).unblockUser(userId);

        mockMvc.perform(put("/api/admin/users/" + userId + "/unblock"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("Gagal menghubungi auth service"))
                .andExpect(jsonPath("$.status").value(503));

        verify(authService, times(1)).unblockUser(userId);
    }
}