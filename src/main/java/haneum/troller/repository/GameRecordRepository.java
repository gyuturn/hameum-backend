package haneum.troller.repository;

import haneum.troller.domain.GameRecord;
import haneum.troller.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRecordRepository extends JpaRepository<GameRecord,String> {

    List<GameRecord> findByLolName(String lolName);
}