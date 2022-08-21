package haneum.troller.repository;

import haneum.troller.domain.ChatRoom;
import haneum.troller.domain.Member;
import haneum.troller.domain.MemberChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberChatRepository extends JpaRepository<MemberChat,Long> {
    List<MemberChat> findByMember(Member member);
}

