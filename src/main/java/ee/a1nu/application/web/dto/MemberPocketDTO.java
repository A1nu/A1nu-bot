package ee.a1nu.application.web.dto;

import ee.a1nu.application.database.entity.Transaction;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberPocketDTO {
    private String pocketId;
    private String userId;
    private String username;
    private String userTag;
    private int currencyAmount;
    private boolean frozen;
    private Transaction lastTransaction;
}
