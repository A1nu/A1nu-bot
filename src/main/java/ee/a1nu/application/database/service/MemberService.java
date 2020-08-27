package ee.a1nu.application.database.service;

import discord4j.common.util.Snowflake;
import ee.a1nu.application.database.entity.Member;
import ee.a1nu.application.database.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.util.Loggers;

@Service
public class MemberService {
    private static final reactor.util.Logger log = Loggers.getLogger(MemberService.class);

    private MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member getOrCreateMember(Snowflake snowflake) {
        if (isMemberExists(snowflake)) {
            return getMember(snowflake);
        } else {
            log.info(String.format("Creating new member with id: %s", snowflake.asString()));
            return createMember(snowflake);
        }
    }

    public Member createMember(Snowflake snowflake) {
        return memberRepository.save(Member.builder().id(snowflake.asLong()).build());
    }

    public Member getMember(Snowflake snowflake) {
        return memberRepository.findMemberById(snowflake.asLong());
    }

    public boolean isMemberExists(Snowflake snowflake) {
        return memberRepository.existsMemberById(snowflake.asLong());
    }
}
