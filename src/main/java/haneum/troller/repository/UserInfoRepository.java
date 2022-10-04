package haneum.troller.repository;


import haneum.troller.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserInfoRepository extends JpaRepository<UserInfo,String> {
    List<UserInfo> findByTrollPossibility(String trollPossibility);
    List<UserInfo> findByTier(String tier);
    List<UserInfo> findByCluster(String cluster);

}
