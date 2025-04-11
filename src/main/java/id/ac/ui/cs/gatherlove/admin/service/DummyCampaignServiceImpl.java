// src/main/java/id/ac/ui/cs/gatherlove/admin/service/impl/DummyCampaignServiceImpl.java
package id.ac.ui.cs.gatherlove.admin.service;

import id.ac.ui.cs.gatherlove.admin.dto.CampaignDTO;
import id.ac.ui.cs.gatherlove.admin.service.CampaignService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DummyCampaignServiceImpl implements CampaignService {

    private final List<CampaignDTO> dummyCampaigns = new ArrayList<>();

    public DummyCampaignServiceImpl() {
        // Inisialisasi data dummy
        // Kampanye pending
        dummyCampaigns.add(new CampaignDTO(
                UUID.randomUUID(), "Bantu Korban Banjir", "Penggalangan dana untuk membantu korban banjir",
                UUID.randomUUID(), "Fundraiser One", new BigDecimal("50000000"), BigDecimal.ZERO,
                LocalDate.now().plusDays(1), LocalDate.now().plusMonths(1), "PENDING",
                new ArrayList<>(), null
        ));
        dummyCampaigns.add(new CampaignDTO(
                UUID.randomUUID(), "Bangun Sekolah", "Penggalangan dana untuk membangun sekolah di daerah terpencil",
                UUID.randomUUID(), "Fundraiser Two", new BigDecimal("100000000"), BigDecimal.ZERO,
                LocalDate.now().plusDays(2), LocalDate.now().plusMonths(2), "PENDING",
                new ArrayList<>(), null
        ));

        // Kampanye aktif
        dummyCampaigns.add(new CampaignDTO(
                UUID.randomUUID(), "Bantuan Medis", "Penggalangan dana untuk biaya pengobatan anak-anak kurang mampu",
                UUID.randomUUID(), "Fundraiser One", new BigDecimal("75000000"), new BigDecimal("25000000"),
                LocalDate.now().minusDays(10), LocalDate.now().plusDays(20), "ACTIVE",
                Arrays.asList("https://example.com/proof1.jpg", "https://example.com/proof2.jpg"), null
        ));
        dummyCampaigns.add(new CampaignDTO(
                UUID.randomUUID(), "Renovasi Masjid", "Penggalangan dana untuk renovasi masjid",
                UUID.randomUUID(), "Fundraiser Two", new BigDecimal("150000000"), new BigDecimal("75000000"),
                LocalDate.now().minusDays(15), LocalDate.now().plusDays(15), "ACTIVE",
                Arrays.asList("https://example.com/proof3.jpg"), null
        ));

        // Kampanye selesai
        dummyCampaigns.add(new CampaignDTO(
                UUID.randomUUID(), "Beasiswa Pendidikan", "Penggalangan dana untuk beasiswa pendidikan",
                UUID.randomUUID(), "Fundraiser One", new BigDecimal("60000000"), new BigDecimal("60000000"),
                LocalDate.now().minusMonths(2), LocalDate.now().minusDays(5), "COMPLETED",
                Arrays.asList("https://example.com/proof4.jpg", "https://example.com/proof5.jpg", "https://example.com/proof6.jpg"), null
        ));
    }

    @Override
    public Long getTotalCampaigns() {
        return (long) dummyCampaigns.size();
    }

    @Override
    public Long getPendingCampaigns() {
        return dummyCampaigns.stream()
                .filter(campaign -> "PENDING".equals(campaign.getStatus()))
                .count();
    }

    @Override
    public Long getActiveCampaigns() {
        return dummyCampaigns.stream()
                .filter(campaign -> "ACTIVE".equals(campaign.getStatus()))
                .count();
    }

    @Override
    public Long getCompletedCampaigns() {
        return dummyCampaigns.stream()
                .filter(campaign -> "COMPLETED".equals(campaign.getStatus()))
                .count();
    }

    @Override
    public List<CampaignDTO> getPendingCampaignsList() {
        return dummyCampaigns.stream()
                .filter(campaign -> "PENDING".equals(campaign.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CampaignDTO> getActiveCampaignsList() {
        return dummyCampaigns.stream()
                .filter(campaign -> "ACTIVE".equals(campaign.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CampaignDTO> getCompletedCampaignsList() {
        return dummyCampaigns.stream()
                .filter(campaign -> "COMPLETED".equals(campaign.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public void verifyCampaign(UUID campaignId, boolean approved, String rejectionReason) {
        dummyCampaigns.stream()
                .filter(campaign -> campaign.getId().equals(campaignId))
                .findFirst()
                .ifPresent(campaign -> {
                    if (approved) {
                        campaign.setStatus("ACTIVE");
                        System.out.println("Kampanye " + campaign.getTitle() + " disetujui");
                    } else {
                        campaign.setStatus("REJECTED");
                        campaign.setRejectionReason(rejectionReason);
                        System.out.println("Kampanye " + campaign.getTitle() + " ditolak dengan alasan: " + rejectionReason);
                    }
                });
    }

    @Override
    public List<String> getFundUsageProof(UUID campaignId) {
        return dummyCampaigns.stream()
                .filter(campaign -> campaign.getId().equals(campaignId))
                .findFirst()
                .map(CampaignDTO::getProofOfFundUsage)
                .orElse(new ArrayList<>());
    }
}