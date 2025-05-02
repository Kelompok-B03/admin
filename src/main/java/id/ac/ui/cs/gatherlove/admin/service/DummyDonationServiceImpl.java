package id.ac.ui.cs.gatherlove.admin.service;

import id.ac.ui.cs.gatherlove.admin.dto.DonationDTO;
import id.ac.ui.cs.gatherlove.admin.service.DonationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DummyDonationServiceImpl implements DonationService {

    private final List<DonationDTO> dummyDonations = new ArrayList<>();

    public DummyDonationServiceImpl() {
        // Inisialisasi data dummy
        UUID campaign1Id = UUID.randomUUID();
        UUID campaign2Id = UUID.randomUUID();

        dummyDonations.add(new DonationDTO(
                UUID.randomUUID(), campaign1Id, "Bantu Korban Banjir",
                UUID.randomUUID(), "Donor One", new BigDecimal("500000"),
                LocalDateTime.now().minusDays(5), "SUCCESSFUL", "Semoga bermanfaat"
        ));
        dummyDonations.add(new DonationDTO(
                UUID.randomUUID(), campaign1Id, "Bantu Korban Banjir",
                UUID.randomUUID(), "Donor Two", new BigDecimal("1000000"),
                LocalDateTime.now().minusDays(4), "SUCCESSFUL", "Untuk saudara-saudara kita"
        ));
        dummyDonations.add(new DonationDTO(
                UUID.randomUUID(), campaign2Id, "Bantuan Medis",
                UUID.randomUUID(), "Donor One", new BigDecimal("750000"),
                LocalDateTime.now().minusDays(3), "SUCCESSFUL", "Semoga lekas sembuh"
        ));
        dummyDonations.add(new DonationDTO(
                UUID.randomUUID(), campaign2Id, "Bantuan Medis",
                UUID.randomUUID(), "Donor Three", new BigDecimal("2000000"),
                LocalDateTime.now().minusDays(2), "SUCCESSFUL", null
        ));
    }

    @Override
    public Long getTotalDonations() {
        return (long) dummyDonations.size();
    }

    @Override
    public BigDecimal getTotalAmountCollected() {
        return dummyDonations.stream()
                .map(DonationDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}