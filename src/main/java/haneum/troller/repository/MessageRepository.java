package haneum.troller.repository;

import haneum.troller.domain.Member;
import haneum.troller.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Long> {
}