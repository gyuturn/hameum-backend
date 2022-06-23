package haneum.troller.repository;

import haneum.troller.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findById(Long memberId) {
        return em.find(Member.class, memberId);
    }

    public Member findByEmail(String email){
        List<Member> memberList = em.createQuery("select m from Member m where m.eMail=:eMail", Member.class)
                .setParameter("eMail", email)
                .getResultList();
        if(memberList.size()!=1){
            throw new IllegalStateException("email과 일치하는 member가 없습니다.");
        }
        return memberList.get(0);
    }

    public List<Member> findByLolName(String lolName) {
        List<Member> memberList = em.createQuery("select m from Member m where m.lolName=:lolName", Member.class)
                .setParameter("lolName", lolName)
                .getResultList();
        return memberList;
    }


}
