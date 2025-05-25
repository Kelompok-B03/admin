package id.ac.ui.cs.gatherlove.admin.facade;

import id.ac.ui.cs.gatherlove.admin.dto.CampaignDTO;
import id.ac.ui.cs.gatherlove.admin.dto.UserDTO;
import id.ac.ui.cs.gatherlove.admin.model.DashboardStatistics;
import id.ac.ui.cs.gatherlove.admin.service.AuthService;
import id.ac.ui.cs.gatherlove.admin.service.CampaignService;
import id.ac.ui.cs.gatherlove.admin.service.DonationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminFacadeTest {

    @Mock
    private AuthService authService;

    @Mock
    private CampaignService campaignService;

    @Mock
    private DonationService donationService;

    private AdminFacade adminFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminFacade = new AdminFacadeImpl(authService, campaignService, donationService);
    }

    @Test
    void getDashboardStatistics_ShouldReturnCombinedStatistics() {
        // Arrange
        when(authService.getTotalUsers()).thenReturn(100L);
        when(campaignService.getTotalCampaigns()).thenReturn(25L);
        when(campaignService.getPendingCampaigns()).thenReturn(5L);
        when(campaignService.getActiveCampaigns()).thenReturn(15L);
        when(campaignService.getCompletedCampaigns()).thenReturn(5L);
        when(donationService.getTotalDonations()).thenReturn(200L);

        // Act
        DashboardStatistics statistics = adminFacade.getDashboardStatistics();

        // Assert
        assertNotNull(statistics);
        assertEquals(100L, statistics.getTotalUsers());
        assertEquals(25L, statistics.getTotalCampaigns());
        assertEquals(5L, statistics.getPendingCampaigns());
        assertEquals(15L, statistics.getActiveCampaigns());
        assertEquals(5L, statistics.getCompletedCampaigns());
        assertEquals(200L, statistics.getTotalDonations());
    }

    @Test
    void getPendingCampaigns_ShouldReturnPendingCampaigns() {
        // Arrange
        CampaignDTO campaign1 = new CampaignDTO(
                UUID.randomUUID(), "Kampanye 1", "Deskripsi", UUID.randomUUID(),
                "Fundraiser 1", new BigDecimal("1000000"), new BigDecimal("0"),
                LocalDate.now(), LocalDate.now().plusDays(30), "PENDING",
                null, null
        );
        CampaignDTO campaign2 = new CampaignDTO(
                UUID.randomUUID(), "Kampanye 2", "Deskripsi", UUID.randomUUID(),
                "Fundraiser 2", new BigDecimal("2000000"), new BigDecimal("0"),
                LocalDate.now(), LocalDate.now().plusDays(30), "PENDING",
                null, null
        );

        when(campaignService.getPendingCampaignsList()).thenReturn(Arrays.asList(campaign1, campaign2));

        // Act
        List<CampaignDTO> pendingCampaigns = adminFacade.getPendingCampaigns();

        // Assert
        assertNotNull(pendingCampaigns);
        assertEquals(2, pendingCampaigns.size());
        assertEquals("Kampanye 1", pendingCampaigns.get(0).getTitle());
        assertEquals("Kampanye 2", pendingCampaigns.get(1).getTitle());
    }

    @Test
    void verifyCampaign_ShouldCallCampaignService() {
        // Arrange
        UUID campaignId = UUID.randomUUID();
        boolean approved = true;
        String reason = null;

        // Act
        adminFacade.verifyCampaign(campaignId, approved, reason);

        // Assert
        verify(campaignService, times(1)).verifyCampaign(campaignId, approved, reason);
    }

    @Test
    void blockUser_ShouldCallAuthService() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String reason = "Melanggar aturan platform";

        // Act
        adminFacade.blockUser(userId, reason);

        // Assert
        verify(authService, times(1)).blockUser(userId, reason);
    }


    @Test
    void getUserById_ShouldReturnUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusMonths(3);
        
        UserDTO expectedUser = new UserDTO();
        expectedUser.setId(userId);
        expectedUser.setEmail("user1@example.com");
        expectedUser.setName("User One");
        expectedUser.setRoles(Arrays.asList("FUNDRAISER"));
        expectedUser.setActive(true);
        expectedUser.setCreatedAt(createdAt);
        expectedUser.setUpdatedAt(createdAt);

        when(authService.getUserById(userId)).thenReturn(expectedUser);

        // Act
        UserDTO result = adminFacade.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("user1@example.com", result.getEmail());
        assertEquals("User One", result.getName());
        assertTrue(result.isActive());
        assertEquals("FUNDRAISER", result.getRoles().get(0));
        verify(authService, times(1)).getUserById(userId);
    }

    @Test
    void unblockUser_ShouldCallAuthService() {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act
        adminFacade.unblockUser(userId);

        // Assert
        verify(authService, times(1)).unblockUser(userId);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        List<UserDTO> expectedUsers = Arrays.asList(
            createTestUserDTO(UUID.randomUUID(), "admin@example.com", "Admin", Arrays.asList("ADMIN"), true),
            createTestUserDTO(UUID.randomUUID(), "user@example.com", "User", Arrays.asList("USER"), true)
        );
        
        when(authService.getAllUsers()).thenReturn(expectedUsers);

        // Act
        List<UserDTO> result = adminFacade.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("admin@example.com", result.get(0).getEmail());
        assertEquals("user@example.com", result.get(1).getEmail());
        verify(authService, times(1)).getAllUsers();
    }

    // Helper method untuk membuat UserDTO
    private UserDTO createTestUserDTO(UUID id, String email, String name, List<String> roles, boolean active) {
        UserDTO user = new UserDTO();
        user.setId(id);
        user.setEmail(email);
        user.setName(name);
        user.setRoles(roles);
        user.setActive(active);
        user.setCreatedAt(LocalDateTime.now().minusMonths(1));
        user.setUpdatedAt(LocalDateTime.now().minusMonths(1));
        return user;
    }
}