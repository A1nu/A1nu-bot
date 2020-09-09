package ee.a1nu.application.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ee.a1nu.application.database.constants.TransactionType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.catalina.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "transaction")
public class Transaction extends BaseEntity {

    @ManyToOne
    @NonNull
    @JsonIgnore
    Pocket pocket;

    @ManyToOne
    @JsonIgnore
    Pocket secondaryPocket;

    @Column(name = "type")
    @NonNull
    TransactionType transactionType;

    @Column(name = "diff")
    @NonNull
    int diff;

    @Column(name = "balance_after")
    @NonNull
    int balanceAfter;

    @Column(name = "description")
    @NonNull
    String description;

    @Column(name = "date")
    @NonNull
    LocalDateTime date;
}
