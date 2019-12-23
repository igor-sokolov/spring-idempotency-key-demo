package blog.help42.springidempotencykeydemo.repository;

import blog.help42.springidempotencykeydemo.model.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByIdempotencyKey(UUID idempotenceKey);
}
