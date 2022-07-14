package haneum.troller.repository;

import haneum.troller.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface MemberRepository extends JpaRepository<Member,Long> {
    Member findByEmail(String email);

    List<Member> findByLolName(String lolName);
}

