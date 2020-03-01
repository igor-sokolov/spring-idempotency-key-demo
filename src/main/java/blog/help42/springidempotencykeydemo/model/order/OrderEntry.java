package blog.help42.springidempotencykeydemo.model.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = OrderEntry.TABLE_NAME)
@Getter
@Setter
@NoArgsConstructor
public class OrderEntry {
    public static final String TABLE_NAME = "order_entries";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @Min(value = 0)
    @NotNull
    private int index;

    @NotBlank
    private String productId;

    @NotNull
    @Min(value = 0)
    private int quantity;

    @NotNull
    @Min(value = 0)
    private BigDecimal price;
}
