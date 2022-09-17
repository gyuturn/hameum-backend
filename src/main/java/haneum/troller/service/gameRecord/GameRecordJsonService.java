package haneum.troller.service.gameRecord;

import haneum.troller.common.callApi.CallApi;
import haneum.troller.domain.GameRecord;
import haneum.troller.repository.GameRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class GameRecordJsonService {
    private final GameRecordRepository gameRecordRepository;

    public String userInfoFilter(Optional<GameRecord> gameRecord,String lolName, String encodedLolName){
        if (gameRecord.isEmpty()) {
            log.info("유저 자체가 db에 존재하지 않음-롤 닉네임:{}",lolName);

            String userInfo = CallApi.GetIncludeParameter("/dataflow/user/infoMachineLearning?lolName=" + encodedLolName).getBody().toString();

            gameRecordRepository.save(GameRecord.builder()
                    .lolName(lolName)
                    .userInfo(userInfo)
                    .build());
            log.info("유저 간략 정보 db 저장");

            return userInfo;
        }
        else if(gameRecord.get().getUserInfo()==null){
            log.info("유저 간략정보가 db에 존재하지 않음-롤 닉네임:{}",lolName);

            String userInfo = CallApi.GetIncludeParameter("/dataflow/user/infoMachineLearning?lolName=" + encodedLolName).getBody().toString();

            GameRecord updatedGameRecord = gameRecord.get().updateUserInfo(userInfo);
            gameRecordRepository.save(updatedGameRecord);
            log.info("유저 간략 정보 db update");

            return userInfo;
        }
        else{
            return gameRecord.get().getUserInfo();
        }
    }

    public String userLineFilter(Optional<GameRecord> gameRecord,String lolName, String encodedLolName){
        if (gameRecord.isEmpty()) {
            log.info("유저 자체가 db에 존재하지 않음-롤 닉네임:{}",lolName);

            String lineInfo = CallApi.GetIncludeParameter("/dataflow/user/lineMachineLearning?lolName=" + encodedLolName).getBody().toString();

            gameRecordRepository.save(GameRecord.builder()
                    .lolName(lolName)
                    .lineInfo(lineInfo)
                    .build());
            log.info("유저 라인 정보 db 저장");

            return lineInfo;
        }
        else if(gameRecord.get().getLineInfo()==null){
            log.info("유저 라인정보가 db에 존재하지 않음-롤 닉네임:{}",lolName);

            String lineInfo = CallApi.GetIncludeParameter("/dataflow/user/lineMachineLearning?lolName=" + encodedLolName).getBody().toString();

            GameRecord updatedGameRecord = gameRecord.get().updateLineInfo(lineInfo);
            gameRecordRepository.save(updatedGameRecord);
            log.info("유저 라인 정보 db update");

            return lineInfo;
        }
        else{
            return gameRecord.get().getLineInfo();
        }
    }

    public String MostChampionFilter(Optional<GameRecord> gameRecord,String lolName, String encodedLolName){
        if (gameRecord.isEmpty()) {
            log.info("유저 자체가 db에 존재하지 않음-롤 닉네임:{}",lolName);

            String mostChampion = CallApi.GetIncludeParameter("/dataflow/user/mostMachineLearning?lolName=" + encodedLolName).getBody().toString();

            gameRecordRepository.save(GameRecord.builder()
                    .lolName(lolName)
                    .mostChampion(mostChampion)
                    .build());
            log.info("유저 모스트챔피언 정보 db 저장");

            return mostChampion;
        }
        else if(gameRecord.get().getMostChampion()==null){
            log.info("유저 모스트챔피언 정보가 db에 존재하지 않음-롤 닉네임:{}",lolName);

            String mostChampion = CallApi.GetIncludeParameter("/dataflow/user/mostMachineLearning?lolName=" + encodedLolName).getBody().toString();

            GameRecord updatedGameRecord = gameRecord.get().updateMostChampion(mostChampion);
            gameRecordRepository.save(updatedGameRecord);
            log.info("유저 모스트챔피언 정보 db update");

            return mostChampion;
        }
        else{
            return gameRecord.get().getMostChampion();
        }
    }

    public String FullRecordFilter(Optional<GameRecord> gameRecord,String lolName, String encodedLolName){
        if (gameRecord.isEmpty()) {
            log.info("유저 자체가 db에 존재하지 않음-롤 닉네임:{}",lolName);

            String fullRecord = CallApi.GetIncludeParameter("/dataflow/user/gameRecordMachineLearning?lolName=" + encodedLolName).getBody().toString();

            gameRecordRepository.save(GameRecord.builder()
                    .lolName(lolName)
                    .fullRecord(fullRecord)
                    .build());
            log.info("유저 전적 정보 db 저장");

            return fullRecord;
        }
        else if(gameRecord.get().getFullRecord()==null){
            log.info("유저 전적 정보가 db에 존재하지 않음-롤 닉네임:{}",lolName);

            String fullRecord = CallApi.GetIncludeParameter("/dataflow/user/gameRecordMachineLearning?lolName=" + encodedLolName).getBody().toString();

            GameRecord updatedGameRecord = gameRecord.get().updateFullRecord(fullRecord);
            gameRecordRepository.save(updatedGameRecord);
            log.info("유저 전적 정보 db update");

            return fullRecord;
        }
        else{
            return gameRecord.get().getFullRecord();
        }
    }
}
