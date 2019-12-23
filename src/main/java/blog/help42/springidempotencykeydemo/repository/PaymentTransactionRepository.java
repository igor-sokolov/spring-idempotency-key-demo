package blog.help42.springidempotencykeydemo.repository;

import blog.help42.springidempotencykeydemo.model.payment.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByIdempotencyKey(UUID idempotenceKey);
}
