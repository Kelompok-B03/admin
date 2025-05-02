package id.ac.ui.cs.gatherlove.admin.service;

import id.ac.ui.cs.gatherlove.admin.dto.TransactionDTO;
import java.util.List;

public interface WalletService {
    List<TransactionDTO> getAllTransactions();
}