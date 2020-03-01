package blog.help42.springidempotencykeydemo.repository;

import blog.help42.springidempotencykeydemo.model.order.Address;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends IdempotencyKeyAwareJpaRepository<Address> {

}
