package blog.help42.springidempotencykeydemo.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@MappedSuperclass
@Data
@NoArgsConstructor
public abstract class IdempotencyKeyEnabled {
    public static final String IDEMPOTENCY_KEY_NAME = "idempotency_key";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @NotNull
    @Column(name = IDEMPOTENCY_KEY_NAME, updatable = false)
    private UUID idempotencyKey;

    public IdempotencyKeyEnabled(UUID idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public abstract String getIdempotencyKeyConstraintName();

    public boolean isIdempotencyKeyConstraint(DataIntegrityViolationException e) {
        final var cause = e.getCause();
        return cause instanceof ConstraintViolationException
                && getIdempotencyKeyConstraintName().equals(((ConstraintViolationException) cause).getConstraintName());
    }
}
