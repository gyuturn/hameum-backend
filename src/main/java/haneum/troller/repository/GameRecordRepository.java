package haneum.troller.repository;

import haneum.troller.domain.GameRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRecordRepository extends JpaRepository<GameRecord,String> {
}