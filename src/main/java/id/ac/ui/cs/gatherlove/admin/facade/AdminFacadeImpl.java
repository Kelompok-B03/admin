package id.ac.ui.cs.gatherlove.admin.facade;

import id.ac.ui.cs.gatherlove.admin.dto.CampaignDTO;
import id.ac.ui.cs.gatherlove.admin.dto.UserDTO;
import id.ac.ui.cs.gatherlove.admin.model.DashboardStatistics;
import id.ac.ui.cs.gatherlove.admin.service.AuthService;
import id.ac.ui.cs.gatherlove.admin.service.CampaignService;
import id.ac.ui.cs.gatherlove.admin.service.DonationService;
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

    @Override
    public DashboardStatistics getDashboardStatistics() {
        return DashboardStatistics.builder()
                .totalUsers(authService.getTotalUsers())
                .totalCampaigns(campaignService.getTotalCampaigns())
                .activeCampaigns(campaignService.getActiveCampaigns())
                .completedCampaigns(campaignService.getCompletedCampaigns())
                .totalDonations(donationService.getTotalDonations())
                .build();
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