package blog.help42.springidempotencykeydemo.model.order;

import blog.help42.springidempotencykeydemo.model.IdempotencyKeyEnabled;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = Order.TABLE_NAME,
        uniqueConstraints = {
                @UniqueConstraint(name = Order.IDEMPOTENCY_KEY_CONSTRAINT,
                        columnNames = {IdempotencyKeyEnabled.IDEMPOTENCY_KEY_NAME})})
@NamedEntityGraph(name = "graph.Order.full", includeAllAttributes = true)
@Getter
@Setter
@NoArgsConstructor
public class Order extends IdempotencyKeyEnabled {
    public static final String TABLE_NAME = "orders";
    public static final String IDEMPOTENCY_KEY_CONSTRAINT = TABLE_NAME + "_" + IdempotencyKeyEnabled.IDEMPOTENCY_KEY_NAME + "_constraint";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "billing_address")
    private Address billingAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shipping_address")
    private Address shippingAddress;

    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    @OrderColumn(name = "index")
    private List<OrderEntry> entries;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "billTo", nullable = false)
    @NotNull
    private Account billTo;

    @Min(value = 0)
    @NotNull
    private BigDecimal subtotal;

    @Min(value = 0)
    @NotNull
    private BigDecimal totalTax;

    @Min(value = 0)
    @NotNull
    private BigDecimal total;

    @Override
    public String getIdempotencyKeyConstraintName() {
        return IDEMPOTENCY_KEY_CONSTRAINT;
    }
}
