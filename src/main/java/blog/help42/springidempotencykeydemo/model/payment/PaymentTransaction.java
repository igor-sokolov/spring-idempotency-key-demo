package blog.help42.springidempotencykeydemo.model.payment;

import blog.help42.springidempotencykeydemo.model.IdempotencyKeyEnabled;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

@Entity
@Table(name = "payment_transactions",
        uniqueConstraints = {
                @UniqueConstraint(name = PaymentTransaction.IDEMPOTENCY_KEY_CONSTRAINT,
                        columnNames = {IdempotencyKeyEnabled.IDEMPOTENCY_KEY_NAME})
        })
@Getter
@Setter
@NoArgsConstructor
public class PaymentTransaction extends IdempotencyKeyEnabled {
    public static final String IDEMPOTENCY_KEY_CONSTRAINT = "payment_transactions_" + IdempotencyKeyEnabled.IDEMPOTENCY_KEY_CONSTRAINT;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @NotNull
    @Column(length = 3)
    private Currency currency;

    @NotNull
    @Min(value = 0)
    @Column
    private BigDecimal amount;

    public PaymentTransaction(@NotNull Currency currency, @NotNull @Min(value = 0) BigDecimal amount, @NotNull UUID idempotencyKey) {
        super(idempotencyKey);
        this.currency = currency;
        this.amount = amount;
    }
}
