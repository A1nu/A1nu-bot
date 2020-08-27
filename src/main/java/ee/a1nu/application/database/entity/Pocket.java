package ee.a1nu.application.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "pocket")
public class Pocket extends BaseEntity {

    @OneToOne
    public Guild guild;

    @OneToOne
    public Member member;

    @Column(name = "frozen")
    public boolean frozen;

    @Column(name = "currency_amount")
    public int currencyAmount;

    @OneToOne
    Transaction lastTransaction;

    @OneToMany
    @JsonIgnore
    List<Transaction> transactionList;
}
