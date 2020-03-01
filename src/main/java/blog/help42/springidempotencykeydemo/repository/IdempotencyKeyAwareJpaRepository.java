package blog.help42.springidempotencykeydemo.repository;

import blog.help42.springidempotencykeydemo.model.IdempotencyKeyEnabled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface IdempotencyKeyAwareJpaRepository<T extends IdempotencyKeyEnabled> extends JpaRepository<T, Long> {
    Optional<T> findByIdempotencyKey(UUID idempotenceKey);
}
