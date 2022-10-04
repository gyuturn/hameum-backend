package haneum.troller.service.machineLearning;

import haneum.troller.domain.UserInfo;
import haneum.troller.dto.ml.DuoDto;
import haneum.troller.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KMeansService {

    private final UserInfoRepository userInfoRepository;

    public List<DuoDto> getDuoDtos(UserInfo userInfo) {
        List<UserInfo> byClusteringList = userInfoRepository.findByCluster(userInfo.getCluster());

        log.info("유저의 클러스터:{}", userInfo.getCluster());
        List<DuoDto> DescTrollUsers = new ArrayList<>();

        for (UserInfo info : byClusteringList) {
            Double troll = Double.valueOf(info.getTrollPossibility());
            DescTrollUsers.add(
                    DuoDto.builder()
                            .lolName(info.getLolName())
                            .ranking(info.getRanking())
                            .tier(info.getTier())
                            .trollPossibility(Double.valueOf(info.getTrollPossibility()))
                            .winRate(info.getWinRate())
                            .build()
            );

        }
        Collections.sort(DescTrollUsers);

        List<DuoDto> fiveUser = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            fiveUser.add(DescTrollUsers.get(i));
        }

        return fiveUser;
    }
}
