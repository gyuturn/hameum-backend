package haneum.troller.repository;

import haneum.troller.domain.Board;
import haneum.troller.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board,Long>, JpaSpecificationExecutor<Board> {

    public final BoardRepository boardRepository = null;

    Board findByTier(String tier);

    List<Board>findByLolName(String lolName);

    Board findById(long id);

}
