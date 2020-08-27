package ee.a1nu.application.database.entity;

import ee.a1nu.application.bot.core.data.CommandCategory;
import ee.a1nu.application.commandConstants.CommandAlias;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
public class BaseCommand extends BaseEntity {
    @Column(name = "category")
    private CommandCategory category;

    @Column(name = "alias")
    CommandAlias alias;

    @Column(name = "enabled")
    boolean isEnabled;

    @ManyToOne
    Guild guild;
}
