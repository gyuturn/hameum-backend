package haneum.troller.repository;

import haneum.troller.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Long> {
    List<Member> findByEmail(String eMail);

    List<Member> findByLolName(String lolName);
}

