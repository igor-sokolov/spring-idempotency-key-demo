package blog.help42.springidempotencykeydemo.repository;

import blog.help42.springidempotencykeydemo.model.order.Account;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends IdempotencyKeyAwareJpaRepository<Account> {

}
