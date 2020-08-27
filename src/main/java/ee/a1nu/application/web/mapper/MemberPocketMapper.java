package ee.a1nu.application.web.mapper;

import discord4j.core.object.entity.Member;
import ee.a1nu.application.database.entity.Pocket;
import ee.a1nu.application.web.dto.MemberPocketDTO;

public class MemberPocketMapper {
    public static MemberPocketDTO map(Pocket pocket, Member member) {
        return  MemberPocketDTO
                    .builder()
                    .pocketId(pocket.getId().toString())
                    .userId(pocket.getMember().getSnowflake().asString())
                    .username(member.getDisplayName())
                    .userTag(member.getTag())
                    .currencyAmount(pocket.getCurrencyAmount())
                    .frozen(pocket.isFrozen())
                    .lastTransaction(pocket.getLastTransaction())
                    .build();
    }
}
