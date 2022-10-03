package haneum.troller.service.fullSearch.userInfo;

import haneum.troller.domain.UserInfo;
import haneum.troller.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserInfoService {
    private final UserInfoRepository userInfoRepository;

    public String FilterTier(String tier) {
        List<UserInfo> userInfoList = userInfoRepository.findByTier(tier);

        Double sumTrollPossibility = Double.valueOf(0);
        for (UserInfo userInfo : userInfoList) {
            String trollPossibility = userInfo.getTrollPossibility();
            if(trollPossibility.equals("0")||trollPossibility.equals("NaN")) continue;
            sumTrollPossibility += Double.valueOf(trollPossibility);
        }
        return String.valueOf(sumTrollPossibility /= userInfoList.size());
    }

}
