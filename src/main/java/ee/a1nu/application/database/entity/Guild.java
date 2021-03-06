package ee.a1nu.application.database.entity;

import discord4j.common.util.Snowflake;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "guild")
public class Guild {
    @Id
    @Column(name = "id")
    Long id;

    @Column(name = "premium")
    Boolean premium;

    public Snowflake getSnowflake() {
        return Snowflake.of(id);
    }

    public void setId(Snowflake snowflake) {
        this.id = snowflake.asLong();
    }
}
