// src/main/java/id/ac/ui/cs/gatherlove/admin/facade/AdminFacadeImpl.java
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminFacadeImpl implements AdminFacade {

    private final AuthService authService;
    private final CampaignService campaignService;
    private final DonationService donationService;
    private final WalletService walletService;

    @Override
    public DashboardStatistics getDashboardStatistics() {
        return DashboardStatistics.builder()
                .totalUsers(authService.getTotalUsers())
                .totalCampaigns(campaignService.getTotalCampaigns())
                .pendingCampaigns(campaignService.getPendingCampaigns())
                .activeCampaigns(campaignService.getActiveCampaigns())
                .completedCampaigns(campaignService.getCompletedCampaigns())
                .totalDonations(donationService.getTotalDonations())
                .totalAmountCollected(donationService.getTotalAmountCollected())
                .build();
    }

    @Override
    public List<CampaignDTO> getPendingCampaigns() {
        return campaignService.getPendingCampaignsList();
    }

    @Override
    public List<CampaignDTO> getActiveCampaigns() {
        return campaignService.getActiveCampaignsList();
    }

    @Override
    public List<CampaignDTO> getCompletedCampaigns() {
        return campaignService.getCompletedCampaignsList();
    }

    @Override
    public void verifyCampaign(UUID campaignId, boolean approved, String rejectionReason) {
        campaignService.verifyCampaign(campaignId, approved, rejectionReason);
    }

    @Override
    public List<TransactionDTO> getTransactionHistory() {
        return walletService.getAllTransactions();
    }

    @Override
    public List<String> getFundUsageProof(UUID campaignId) {
        return campaignService.getFundUsageProof(campaignId);
    }

    @Override
    public void blockUser(UUID userId, String reason) {
        authService.blockUser(userId, reason);
    }
    
    @Override
    public void unblockUser(UUID userId) {
        authService.unblockUser(userId);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return authService.getAllUsers();
    }

    @Override
    public UserDTO getUserById(UUID userId) {
        return authService.getUserById(userId);
    }
}