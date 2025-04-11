package id.ac.ui.cs.gatherlove.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDTO {
    private UUID id;
    private String title;
    private String description;
    private UUID fundraiserId;
    private String fundraiserName;
    private BigDecimal targetAmount;
    private BigDecimal collectedAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // PENDING, ACTIVE, COMPLETED, REJECTED
    private List<String> proofOfFundUsage;
    private String rejectionReason;
}