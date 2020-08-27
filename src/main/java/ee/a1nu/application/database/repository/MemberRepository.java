package ee.a1nu.application.database.repository;

import ee.a1nu.application.database.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsMemberById(Long id);
    Member findMemberById(Long id);
}
