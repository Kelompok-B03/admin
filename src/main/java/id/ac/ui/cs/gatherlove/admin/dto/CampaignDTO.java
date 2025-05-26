package id.ac.ui.cs.gatherlove.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDTO {
    private String campaignId;
    private String title;
    private String description;
    private String fundraiserId;
    private String fundraiserName; // Perlu didapatkan dari service lain
    private int targetAmount; 
    private int fundsCollected;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Boolean withdrawed;
    private String usageProofLink;
    
    // Method helper untuk mendapatkan proofOfFundUsage sebagai List
    public List<String> getProofOfFundUsage() {
        return usageProofLink != null ? 
            Collections.singletonList(usageProofLink) : 
            Collections.emptyList();
    }
}