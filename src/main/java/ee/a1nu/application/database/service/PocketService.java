package ee.a1nu.application.database.service;

import discord4j.common.util.Snowflake;
import ee.a1nu.application.database.constants.TransactionType;
import ee.a1nu.application.database.entity.Pocket;
import ee.a1nu.application.database.repository.GuildEntityRepository;
import ee.a1nu.application.database.repository.PocketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PocketService {
    private final PocketRepository pocketRepository;
    private final MemberService memberService;
    private final GuildEntityRepository guildEntityRepository;
    private final TransactionService transactionService;

    @Autowired
    public PocketService(
            PocketRepository pocketRepository,
            MemberService memberService,
            GuildEntityRepository guildEntityRepository,
            TransactionService transactionService
    ) {
        this.pocketRepository = pocketRepository;
        this.memberService = memberService;
        this.guildEntityRepository = guildEntityRepository;
        this.transactionService = transactionService;
    }

    public Pocket getOrCreatePocket(Long memberId, Long guildId) {
        if (isPocketExists(memberId, guildId)) {
            return getPocket(memberId, guildId);
        } else {
            return createPocket(memberId, guildId);
        }
    }

    public Pocket createPocket(Long memberId, Long guildId) {
        return pocketRepository.save(
                Pocket
                        .builder()
                        .member(memberService.getOrCreateMember(Snowflake.of(memberId)))
                        .frozen(false)
                        .currencyAmount(0)
                        .guild(guildEntityRepository.getOne(guildId))
                        .build()
        );
    }

    public boolean isPocketExists(Long memberId, Long guildId) {
        return pocketRepository.existsPocketByMember_IdAndGuild_Id(memberId, guildId);
    }

    public Pocket getPocket(Long memberId, Long guildId) {
        return pocketRepository.findPocketByMember_IdAndGuild_Id(memberId, guildId);
    }

    public boolean validateOperation(Pocket pocket, int diff) {
        return pocket.getCurrencyAmount() + diff >= 0;
    }

    public Pocket updatePocket(Pocket pocket, int diff, boolean frozen, String description, TransactionType transactionType) {
        pocket.setCurrencyAmount(pocket.getCurrencyAmount() + diff);
        pocket.setFrozen(frozen);
        pocket.setLastTransaction(transactionService.createTransaction(pocket, diff, transactionType, description));
        return pocketRepository.save(pocket);
    }
}
