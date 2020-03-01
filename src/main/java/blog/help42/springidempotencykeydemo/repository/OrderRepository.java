package blog.help42.springidempotencykeydemo.repository;

import blog.help42.springidempotencykeydemo.model.order.Order;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends IdempotencyKeyAwareJpaRepository<Order> {
}
