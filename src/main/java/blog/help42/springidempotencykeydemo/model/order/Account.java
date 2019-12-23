package blog.help42.springidempotencykeydemo.model.order;

import blog.help42.springidempotencykeydemo.model.IdempotencyKeyEnabled;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = Account.TABLE_NAME,
        uniqueConstraints = {
                @UniqueConstraint(
                        name = Account.IDEMPOTENCY_KEY_CONSTRAINT,
                        columnNames = {IdempotencyKeyEnabled.IDEMPOTENCY_KEY_NAME})})
@Getter
@Setter
@NoArgsConstructor
public class Account extends IdempotencyKeyEnabled {
    public static final String TABLE_NAME = "accounts";
    public static final String IDEMPOTENCY_KEY_CONSTRAINT = TABLE_NAME + "_" + IdempotencyKeyEnabled.IDEMPOTENCY_KEY_CONSTRAINT;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank
    @Length(max = 255)
    private String name;
}
