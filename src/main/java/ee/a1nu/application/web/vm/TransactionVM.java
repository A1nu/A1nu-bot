package ee.a1nu.application.web.vm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionVM {
    Long guildId;
    Long memberId;
    Long pocketId;
    String description;
    int diff;
    boolean frozen;
}
