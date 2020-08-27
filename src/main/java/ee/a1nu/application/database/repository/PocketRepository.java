package ee.a1nu.application.database.repository;

import ee.a1nu.application.database.entity.Pocket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PocketRepository extends JpaRepository<Pocket, Long> {
    boolean existsPocketByMember_IdAndGuild_Id(Long memberId, Long guildEntityId);
    Pocket findPocketByMember_IdAndGuild_Id(Long memberId, Long guildEntityId);
}
