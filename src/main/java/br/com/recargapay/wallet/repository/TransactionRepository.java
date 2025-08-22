package br.com.recargapay.wallet.repository;

import br.com.recargapay.wallet.models.TransactionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionModel, UUID> {

    @Query("""
    SELECT t
    FROM TransactionModel t
    WHERE t.wallet.id = :walletId
      AND t.createdAt >= :startOfDay
      AND t.createdAt < :startOfNextDay
    ORDER BY t.createdAt DESC
""")
    List<TransactionModel> findByCreatedAtBetween(UUID walletId, LocalDateTime startOfDay, LocalDateTime startOfNextDay);
}

