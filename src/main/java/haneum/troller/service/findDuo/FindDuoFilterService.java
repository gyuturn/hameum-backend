package haneum.troller.service.findDuo;


import haneum.troller.domain.Board;
import haneum.troller.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Service
public class FindDuoFilterService {

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    public Board board;

    public void boardFilter(String position){
        EntityManager entityManager = null;
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(Board.class);
        Root<Board> boardRoot = criteriaQuery.from(Board.class);
        Predicate line = null;
        if (position.compareTo("TOP") == 0)
            line = criteriaBuilder.equal(boardRoot.get("position"), "TOP");
        else if (position.compareTo("JUNGLE") == 0)
            line = criteriaBuilder.equal(boardRoot.get("position"), "JUNGLE");
        else if (position.compareTo("MID") == 0)
            line = criteriaBuilder.equal(boardRoot.get("position"), "MID");
        else if (position.compareTo("BOTTOM") == 0)
            line = criteriaBuilder.equal(boardRoot.get("position"), "BOTTOM");
        else if (position.compareTo("UTILITY") == 0)
            line = criteriaBuilder.equal(boardRoot.get("position"), "UTILITY");
        javax.persistence.criteria.Order kdaOrder = criteriaBuilder.desc(boardRoot.get("kda"));
        criteriaQuery.select(boardRoot)
                .where(line)
                .orderBy(kdaOrder);
    }


}
