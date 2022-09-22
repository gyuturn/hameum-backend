package haneum.troller.controller.fullSearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import haneum.troller.common.callApi.CallApi;
import haneum.troller.common.exception.exceptions.LolApiToJsonException;
import haneum.troller.domain.GameRecord;
import haneum.troller.dto.gameRecord.GameRecordDto;
import haneum.troller.dto.linePrefer.LinePreferenceDto;
import haneum.troller.dto.mostChampion.MostThreeChampionDto;
import haneum.troller.dto.myPage.MyPageDto;
import haneum.troller.repository.GameRecordRepository;
import haneum.troller.service.fullSearch.GameRecord.GameRecordService;
import haneum.troller.service.fullSearch.linePreference.LinePreferenceService;
import haneum.troller.service.fullSearch.mostThreeChampion.MostThreeChampionService;
import haneum.troller.service.gameRecord.GameRecordJsonService;
import haneum.troller.service.mypage.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Optional;

@Tag(name="userGameRecord",description = "유저의 게임전적 조회시 사옹되는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search/")
@Slf4j
public class FullSearchController {
    private final GameRecordRepository gameRecordRepository;
    private final GameRecordJsonService gameRecordJsonService;

    @Operation(summary = "유저의 간략한정보 api", description = "전적검색/마이페이지에서 간략한 유저 정보를 알려주는 기능")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",description = "정상적 조회"),
                    @ApiResponse(responseCode = "404",description = "해당 유저가 없음")
            }
    )
    @GetMapping("user/info")
    public ResponseEntity getTokenForMyPage(@RequestParam(value = "lolName") String lolName) throws UnsupportedEncodingException, JSONException {
        log.info("유저 간략정보 조회-롤 닉네임:{}", lolName);
        Optional<GameRecord> gameRecord = gameRecordRepository.findById(lolName);
        String encodedLolName = URLEncoder.encode(lolName, "utf-8");
        String userInfo = gameRecordJsonService.userInfoFilter(gameRecord, lolName, encodedLolName);
        JSONObject userInfoJson = new JSONObject(userInfo);
        return new ResponseEntity(userInfoJson.toString(4), HttpStatus.OK);
    }

    @Operation(summary = "유저의 라인정보(포지션) api", description = "유저의 line정보와 해당 라인에 대한 판수를 json return")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",description = "정상적 조회"),
                    @ApiResponse(responseCode = "404",description = "해당 유저가 없음"),
                    @ApiResponse(responseCode = "503",description = "롤 api 호출시 로직에러")
            }
    )
    @Parameter(name="lolName",description = "롤네임")
    @GetMapping("user/line")
    public ResponseEntity getTokenForLine(@RequestParam(value = "lolName") String lolName, @RequestParam(value = "type", required = false, defaultValue = "false") String type)
            throws IOException, JSONException {
        log.info("유저 라인 조회-롤 닉네임:{}", lolName);
        Optional<GameRecord> gameRecord = gameRecordRepository.findById(lolName);
        String encodedLolName = URLEncoder.encode(lolName, "utf-8");

        String lineInfo = gameRecordJsonService.userLineFilter(gameRecord, lolName, encodedLolName);
        JSONObject lineInfoJson = new JSONObject(lineInfo);
        return new ResponseEntity(lineInfoJson.toString(4), HttpStatus.OK);
    }



    @Operation(summary = "유저의 모스트 챔피언 api", description = "유저의 most챔피언과 그에 해당하는 전적정보를 알려줌")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",description = "정상적 조회"),
                    @ApiResponse(responseCode = "404",description = "해당 유저가 없음"),
                    @ApiResponse(responseCode = "503",description = "롤 api 호출시 로직에러")
            }
    )
    @Parameter(name="lolName",description = "롤네임")
    @GetMapping("user/most")
    public ResponseEntity getTokenForMost(@RequestParam(value = "lolName") String lolName, @RequestParam(value = "type", required = false, defaultValue = "false") String type) throws UnsupportedEncodingException, JSONException {
        log.info("유저 모스트챔피언 조회-롤 닉네임:{}", lolName);
        Optional<GameRecord> gameRecord = gameRecordRepository.findById(lolName);
        String encodedLolName = URLEncoder.encode(lolName, "utf-8");
        String mostChampion = gameRecordJsonService.MostChampionFilter(gameRecord, lolName, encodedLolName);
        JSONObject mostChampJson = new JSONObject(mostChampion);
        return new ResponseEntity(mostChampJson.toString(4), HttpStatus.OK);

    }


    @Operation(summary = "유저의 20게임 전적정보 api", description = "유저의 20게임에 대한 전적정보를 알려줌")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",description = "정상적 조회"),
                    @ApiResponse(responseCode = "404",description = "해당 유저가 없음"),
                    @ApiResponse(responseCode = "503",description = "롤 api 호출시 로직에러")
            }
    )
    @Parameter(name="lolName",description = "롤네임")
    @GetMapping("user/gameRecord")
    public ResponseEntity getTokenForGameRecord(@RequestParam(value = "lolName") String lolName, @RequestParam(value = "count", required = false, defaultValue = "20") int count) throws UnsupportedEncodingException, JSONException {
        log.info("유저 전적검색 조회-롤 닉네임:{}", lolName);
        Optional<GameRecord> gameRecord = gameRecordRepository.findById(lolName);
        String encodedLolName = URLEncoder.encode(lolName, "utf-8");
        String fullRecord = gameRecordJsonService.FullRecordFilter(gameRecord, lolName, encodedLolName);
        JSONObject fullRecordJson = new JSONObject(fullRecord);
        return new ResponseEntity(fullRecordJson.toString(4), HttpStatus.OK);
    }

}
