package ee.a1nu.application.database.service;

import ee.a1nu.application.database.constants.TransactionType;
import ee.a1nu.application.database.entity.Pocket;
import ee.a1nu.application.database.entity.Transaction;
import ee.a1nu.application.database.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public boolean isTransactionsExists(Pocket pocket) {
        return transactionRepository.existsTransactionsByPocket(pocket);
    }

    public List<Transaction> getAllTransactions(Pocket pocket) {
        return transactionRepository.findTransactionsByPocket(pocket);
    }

    public Transaction createTransaction(Pocket pocket, int diff, TransactionType transactionType, String description) {
        return transactionRepository.save(
                Transaction
                        .builder()
                        .pocket(pocket)
                        .transactionType(transactionType)
                        .diff(diff)
                        .balanceAfter(pocket.getCurrencyAmount())
                        .description(description)
                        .date(LocalDateTime.now())
                        .build()
        );
    }
}
