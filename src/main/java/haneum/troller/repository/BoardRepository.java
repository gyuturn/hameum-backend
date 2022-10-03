package haneum.troller.repository;

import haneum.troller.domain.Board;
import haneum.troller.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board,Long> {
    List<Board>findByLolName(String lolName);
}
