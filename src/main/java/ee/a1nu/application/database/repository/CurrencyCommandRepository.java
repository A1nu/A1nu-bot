package ee.a1nu.application.database.repository;

import ee.a1nu.application.database.entity.CurrencyCommand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyCommandRepository extends JpaRepository<CurrencyCommand, Long> {
    boolean existsCurrencyCommandByGuild_Id(Long id);
    CurrencyCommand findCurrencyCommandByGuild_Id(Long id);
}
