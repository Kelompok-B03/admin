// src/main/java/id/ac/ui/cs/gatherlove/admin/service/DonationService.java
package id.ac.ui.cs.gatherlove.admin.service;

import id.ac.ui.cs.gatherlove.admin.dto.DonationDTO;
import java.math.BigDecimal;
import java.util.List;

public interface DonationService {
    Long getTotalDonations();
    BigDecimal getTotalAmountCollected();
}