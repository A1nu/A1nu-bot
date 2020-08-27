package ee.a1nu.application.database.repository;

import ee.a1nu.application.database.entity.Guild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuildEntityRepository extends JpaRepository<Guild, Long> {
    boolean existsGuildById(Long id);
}
