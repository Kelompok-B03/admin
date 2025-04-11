package id.ac.ui.cs.gatherlove.admin.service;

import id.ac.ui.cs.gatherlove.admin.dto.TransactionDTO;
import id.ac.ui.cs.gatherlove.admin.service.WalletService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DummyWalletServiceImpl implements WalletService {

    private final List<TransactionDTO> dummyTransactions = new ArrayList<>();

    public DummyWalletServiceImpl() {
        // Inisialisasi data dummy
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();
        UUID user3Id = UUID.randomUUID();

        // Deposit
        dummyTransactions.add(new TransactionDTO(
                UUID.randomUUID(), user1Id, "Donor One",
                new BigDecimal("1000000"), LocalDateTime.now().minusDays(10),
                "DEPOSIT", "SUCCESSFUL", "Deposit saldo"
        ));
        dummyTransactions.add(new TransactionDTO(
                UUID.randomUUID(), user2Id, "Donor Two",
                new BigDecimal("2000000"), LocalDateTime.now().minusDays(9),
                "DEPOSIT", "SUCCESSFUL", "Deposit saldo"
        ));

        // Donation
        dummyTransactions.add(new TransactionDTO(
                UUID.randomUUID(), user1Id, "Donor One",
                new BigDecimal("500000"), LocalDateTime.now().minusDays(5),
                "DONATION", "SUCCESSFUL", "Donasi ke kampanye Bantu Korban Banjir"
        ));
        dummyTransactions.add(new TransactionDTO(
                UUID.randomUUID(), user2Id, "Donor Two",
                new BigDecimal("1000000"), LocalDateTime.now().minusDays(4),
                "DONATION", "SUCCESSFUL", "Donasi ke kampanye Bantu Korban Banjir"
        ));
        dummyTransactions.add(new TransactionDTO(
                UUID.randomUUID(), user1Id, "Donor One",
                new BigDecimal("750000"), LocalDateTime.now().minusDays(3),
                "DONATION", "SUCCESSFUL", "Donasi ke kampanye Bantuan Medis"
        ));

        // Withdrawal
        dummyTransactions.add(new TransactionDTO(
                UUID.randomUUID(), user3Id, "Fundraiser One",
                new BigDecimal("3000000"), LocalDateTime.now().minusDays(1),
                "WITHDRAWAL", "SUCCESSFUL", "Penarikan dana kampanye"
        ));
    }

    @Override
    public List<TransactionDTO> getAllTransactions() {
        return new ArrayList<>(dummyTransactions);
    }
}