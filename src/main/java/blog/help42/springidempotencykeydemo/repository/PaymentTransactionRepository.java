package blog.help42.springidempotencykeydemo.repository;

import blog.help42.springidempotencykeydemo.model.payment.PaymentTransaction;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTransactionRepository extends IdempotencyKeyAwareJpaRepository<PaymentTransaction> {

}
