package blog.help42.springidempotencykeydemo.model.order;

import blog.help42.springidempotencykeydemo.model.IdempotencyKeyEnabled;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = Address.TABLE_NAME,
        uniqueConstraints = {
                @UniqueConstraint(
                        name = Address.IDEMPOTENCY_KEY_CONSTRAINT,
                        columnNames = {IdempotencyKeyEnabled.IDEMPOTENCY_KEY_NAME})})
@Getter
@Setter
@NoArgsConstructor
public class Address extends IdempotencyKeyEnabled {
    public static final String TABLE_NAME = "addresses";
    public static final String IDEMPOTENCY_KEY_CONSTRAINT = TABLE_NAME + "_" + IdempotencyKeyEnabled.IDEMPOTENCY_KEY_NAME + "_constraint";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @NotBlank
    @Length(min = 3, max = 255)
    private String line1;

    @Length(min = 3, max = 255)
    private String line2;

    @NotBlank
    @Length(min = 3, max = 255)
    private String city;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{2}$")
    private String country;

    @Pattern(regexp = "^[A-Z]{2}-[0-9A-Z]{1,3}$")
    private String region;

    @NotBlank
    @Length(min = 3, max = 10)
    private String zipcode;

    @Override
    public String getIdempotencyKeyConstraintName() {
        return IDEMPOTENCY_KEY_CONSTRAINT;
    }
}
