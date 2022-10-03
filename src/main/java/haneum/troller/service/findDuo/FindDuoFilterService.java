package haneum.troller.service.findDuo;


import haneum.troller.domain.Board;
import haneum.troller.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class FindDuoFilterService {

    @Autowired
    private BoardRepository boardRepository;
    public Board board;
    public EntityManager entityManager;
    public void boardFilter(String position, String filter){

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
        javax.persistence.criteria.Order winOrder = criteriaBuilder.desc(boardRoot.get("win"));
        if (position != null && filter.compareTo("KDA") == 0) { // 포지션 && KDA
            criteriaQuery.select(boardRoot)
                    .where(line)
                    .orderBy(kdaOrder);
        }
        else if (position != null && filter.compareTo("WIN") == 0){ // 포지션 && WIN
            criteriaQuery.select(boardRoot)
                    .where(line)
                    .orderBy(winOrder);
        }
        else if (position == null && filter.compareTo("KDA") == 0){ // 포지션 X && KDA
            criteriaQuery.select(boardRoot)
                    .orderBy(kdaOrder);
        }
        else if (position == null && filter.compareTo("WIN") == 0){ // 포지션 X && KDA
            criteriaQuery.select(boardRoot)
                    .orderBy(winOrder);
        }
        else if (position != null && (filter.compareTo("WIN") != 0 && filter.compareTo("KDA") != 0)){ // 포지션 X && KDA
            criteriaQuery.select(boardRoot)
                    .orderBy(winOrder);
        }
    }


}
