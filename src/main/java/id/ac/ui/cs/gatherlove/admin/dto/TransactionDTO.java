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
public class TransactionDTO {
    private UUID id;
    private UUID userId;
    private String username;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String type; // DEPOSIT, WITHDRAWAL, DONATION
    private String status; // SUCCESSFUL, PENDING, FAILED
    private String description;
}