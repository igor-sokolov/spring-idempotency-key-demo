package blog.help42.springidempotencykeydemo.repository;

import blog.help42.springidempotencykeydemo.model.order.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByIdempotencyKey(UUID idempotenceKey);
}
