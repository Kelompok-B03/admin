package id.ac.ui.cs.gatherlove.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonationDTO {
    private UUID id;
    private UUID campaignId;
    private String campaignTitle;
    private UUID donorId;
    private String donorName;
    private BigDecimal amount;
    private LocalDateTime donationDate;
    private String status; // SUCCESSFUL, PENDING, FAILED
    private String message;
}