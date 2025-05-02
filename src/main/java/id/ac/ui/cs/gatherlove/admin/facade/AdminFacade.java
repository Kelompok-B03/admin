package id.ac.ui.cs.gatherlove.admin.facade;

import id.ac.ui.cs.gatherlove.admin.dto.CampaignDTO;
import id.ac.ui.cs.gatherlove.admin.dto.DonationDTO;
import id.ac.ui.cs.gatherlove.admin.dto.TransactionDTO;
import id.ac.ui.cs.gatherlove.admin.dto.UserDTO;
import id.ac.ui.cs.gatherlove.admin.model.DashboardStatistics;

import java.util.List;
import java.util.UUID;

public interface AdminFacade {
    /**
     * Mendapatkan statistik dashboard untuk admin
     */
    DashboardStatistics getDashboardStatistics();

    /**
     * Mendapatkan daftar kampanye yang menunggu verifikasi
     */
    List<CampaignDTO> getPendingCampaigns();

    /**
     * Mendapatkan daftar kampanye aktif
     */
    List<CampaignDTO> getActiveCampaigns();

    /**
     * Mendapatkan daftar kampanye yang sudah selesai
     */
    List<CampaignDTO> getCompletedCampaigns();

    /**
     * Memverifikasi kampanye
     */
    void verifyCampaign(UUID campaignId, boolean approved, String rejectionReason);

    /**
     * Mendapatkan riwayat transaksi donasi
     */
    List<TransactionDTO> getTransactionHistory();

    /**
     * Mendapatkan bukti penggunaan dana kampanye
     */
    List<String> getFundUsageProof(UUID campaignId);

    /**
     * Memblokir pengguna
     */
    void blockUser(UUID userId, String reason);

    /**
     * Mendapatkan daftar pengguna
     */
    List<UserDTO> getAllUsers();
}