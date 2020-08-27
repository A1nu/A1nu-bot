package ee.a1nu.application.database.repository;

import ee.a1nu.application.database.entity.Pocket;
import ee.a1nu.application.database.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    boolean existsTransactionsByPocket(Pocket pocket);
    List<Transaction> findTransactionsByPocket(Pocket pocket);
}
