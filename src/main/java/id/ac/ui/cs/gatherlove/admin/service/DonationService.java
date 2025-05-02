package id.ac.ui.cs.gatherlove.admin.service;

import id.ac.ui.cs.gatherlove.admin.dto.DonationDTO;
import java.math.BigDecimal;
import java.util.List;

public interface DonationService {
    Long getTotalDonations();
    BigDecimal getTotalAmountCollected();
}