package id.ac.ui.cs.gatherlove.admin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatistics {
    private Long totalUsers;
    private Long totalCampaigns;
    private Long pendingCampaigns;
    private Long activeCampaigns;
    private Long completedCampaigns;
    private Long totalDonations;
    private BigDecimal totalAmountCollected;
}