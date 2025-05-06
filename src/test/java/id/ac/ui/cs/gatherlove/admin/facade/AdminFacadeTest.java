package id.ac.ui.cs.gatherlove.admin.facade;

import id.ac.ui.cs.gatherlove.admin.dto.CampaignDTO;
import id.ac.ui.cs.gatherlove.admin.dto.DonationDTO;
import id.ac.ui.cs.gatherlove.admin.dto.TransactionDTO;
import id.ac.ui.cs.gatherlove.admin.dto.UserDTO;
import id.ac.ui.cs.gatherlove.admin.model.DashboardStatistics;
import id.ac.ui.cs.gatherlove.admin.service.AuthService;
import id.ac.ui.cs.gatherlove.admin.service.CampaignService;
import id.ac.ui.cs.gatherlove.admin.service.DonationService;
import id.ac.ui.cs.gatherlove.admin.service.WalletService;
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

    @Mock
    private WalletService walletService;

    private AdminFacade adminFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminFacade = new AdminFacadeImpl(authService, campaignService, donationService, walletService);
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
        when(donationService.getTotalAmountCollected()).thenReturn(new BigDecimal("5000000"));

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
        assertEquals(new BigDecimal("5000000"), statistics.getTotalAmountCollected());
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
    void getTransactionHistory_ShouldReturnTransactionHistory() {
        // Arrange
        TransactionDTO transaction1 = new TransactionDTO(
                UUID.randomUUID(), UUID.randomUUID(), "User 1",
                new BigDecimal("100000"), LocalDateTime.now(),
                "DONATION", "SUCCESSFUL", "Donasi kampanye X"
        );
        TransactionDTO transaction2 = new TransactionDTO(
                UUID.randomUUID(), UUID.randomUUID(), "User 2",
                new BigDecimal("200000"), LocalDateTime.now(),
                "DONATION", "SUCCESSFUL", "Donasi kampanye Y"
        );

        when(walletService.getAllTransactions()).thenReturn(Arrays.asList(transaction1, transaction2));

        // Act
        List<TransactionDTO> transactions = adminFacade.getTransactionHistory();

        // Assert
        assertNotNull(transactions);
        assertEquals(2, transactions.size());
        assertEquals("User 1", transactions.get(0).getUsername());
        assertEquals("User 2", transactions.get(1).getUsername());
    }

    @Test
    void getUserById_ShouldReturnUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserDTO expectedUser = new UserDTO(
                userId, "user1", "user1@example.com", "User One",
                "FUNDRAISER", "ACTIVE", LocalDateTime.now().minusMonths(3)
        );

        when(authService.getUserById(userId)).thenReturn(expectedUser);

        // Act
        UserDTO result = adminFacade.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("user1", result.getUsername());
        verify(authService, times(1)).getUserById(userId);
    }
}