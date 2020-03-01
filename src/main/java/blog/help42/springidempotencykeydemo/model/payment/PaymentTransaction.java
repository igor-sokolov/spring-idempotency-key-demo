package blog.help42.springidempotencykeydemo.model.payment;

import blog.help42.springidempotencykeydemo.model.IdempotencyKeyEnabled;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

@Entity
@Table(name = PaymentTransaction.TABLE_NAME,
        uniqueConstraints = {
                @UniqueConstraint(name = PaymentTransaction.IDEMPOTENCY_KEY_CONSTRAINT,
                        columnNames = {IdempotencyKeyEnabled.IDEMPOTENCY_KEY_NAME})
        })
@Getter
@Setter
@NoArgsConstructor
public class PaymentTransaction extends IdempotencyKeyEnabled {
    public static final String TABLE_NAME = "payment_transactions";
    public static final String IDEMPOTENCY_KEY_CONSTRAINT = TABLE_NAME + "_" + IdempotencyKeyEnabled.IDEMPOTENCY_KEY_NAME + "_constraint";

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

    @Override
    public String getIdempotencyKeyConstraintName() {
        return IDEMPOTENCY_KEY_CONSTRAINT;
    }
}
