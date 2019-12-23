package blog.help42.springidempotencykeydemo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@MappedSuperclass
@Data
@NoArgsConstructor
public abstract class IdempotencyKeyEnabled {
    public static final String IDEMPOTENCY_KEY_NAME = "idempotency_key";
    public static final String IDEMPOTENCY_KEY_CONSTRAINT = IDEMPOTENCY_KEY_NAME + "_constraint";

    @NotNull
    @Column(name = IDEMPOTENCY_KEY_NAME, updatable = false)
    private UUID idempotencyKey;

    public IdempotencyKeyEnabled(UUID idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}
