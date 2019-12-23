package blog.help42.springidempotencykeydemo.repository;

import blog.help42.springidempotencykeydemo.model.order.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByIdempotencyKey(UUID idempotenceKey);
}
