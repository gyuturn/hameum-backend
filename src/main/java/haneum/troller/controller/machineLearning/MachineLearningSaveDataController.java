package haneum.troller.controller.machineLearning;


import haneum.troller.common.exception.exceptions.LolApiToJsonException;
import haneum.troller.dto.gameRecord.GameRecordDto;
import haneum.troller.dto.linePrefer.LinePreferenceDto;
import haneum.troller.dto.mostChampion.MostThreeChampionDto;
import haneum.troller.service.fullSearch.GameRecord.GameRecordService;
import haneum.troller.service.fullSearch.linePreference.LinePreferenceService;
import haneum.troller.service.fullSearch.mostThreeChampion.MostThreeChampionService;
import haneum.troller.service.machineLearning.GameRecordMachineLearningService;
import haneum.troller.service.mypage.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;


@Tag(name="userGameRecord",description = "유저의 게임전적 조회시 사옹되는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search/")
@Slf4j
public class MachineLearningSaveDataController {

    private final LinePreferenceService linePreferenceService;
    private final GameRecordMachineLearningService gameRecordMachineLearningService;
    private final MostThreeChampionService mostThreeChampionService;

    @Operation(summary = "유저의 라인정보(포지션) 머신러닝 데이터 api", description = "유저의 line정보와 해당 라인에 대한 판수를 json return")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",description = "정상적 조회"),
                    @ApiResponse(responseCode = "404",description = "해당 유저가 없음"),
                    @ApiResponse(responseCode = "503",description = "롤 api 호출시 로직에러")
            }
    )
    @Parameter(name="lolName",description = "롤네임")
    @GetMapping("user/lineMachineLearning")
    public ResponseEntity getTokenForLine(@RequestParam(value = "lolName") String lolName, @RequestParam(value = "count", required = false, defaultValue = "20") int count)
            throws LolApiToJsonException, org.json.simple.parser.ParseException, IOException {
        LinePreferenceDto linePreferenceDto = null;
        linePreferenceDto = linePreferenceService.getLinePreferenceDto(lolName, count);
//        try {
//            linePreferenceDto = linePreferenceService.getLinePreferenceDto(lolName);
//        } catch (Exception e) {
//            throw new LolApiToJsonException("롤 api에 호출시 에러");
//        }
        return new ResponseEntity(linePreferenceDto, HttpStatus.OK);
    }



    @Operation(summary = "유저의 모스트 챔피언 머신러닝 데이터 콜렉트 api", description = "유저의 most챔피언과 그에 해당하는 전적정보를 알려줌")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",description = "정상적 조회"),
                    @ApiResponse(responseCode = "404",description = "해당 유저가 없음"),
                    @ApiResponse(responseCode = "503",description = "롤 api 호출시 로직에러")
            }
    )
    @Parameter(name="lolName",description = "롤네임")
    @GetMapping("user/mostMachineLearning")
    public ResponseEntity getTokenForMost(@RequestParam(value = "lolName") String lolName, @RequestParam(value = "count", required = false, defaultValue = "20") int count) throws LolApiToJsonException {
        MostThreeChampionDto mostThreeChampionDto = null;
        try{
            mostThreeChampionDto = mostThreeChampionService.getMostThreeChampionDto(lolName, count);
        } catch (Exception e) {
            throw new LolApiToJsonException("롤 api에 호출시 에러");
        }
        return new ResponseEntity(mostThreeChampionDto, HttpStatus.OK);
    }


    @Operation(summary = "유저의 게임 전적정보 머신러닝 데이터 콜렉트 api", description = "유저의 게임에 대한 전적정보를 알려줌")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",description = "정상적 조회"),
                    @ApiResponse(responseCode = "404",description = "해당 유저가 없음"),
                    @ApiResponse(responseCode = "503",description = "롤 api 호출시 로직에러")
            }
    )
    @Parameter(name="lolName",description = "롤네임")
    @GetMapping("user/gameRecordMachineLearning")
    public ResponseEntity getTokenForGameRecord(@RequestParam(value = "lolName") String lolName, @RequestParam(value = "gameNumber", required = false, defaultValue = "20") int count) throws LolApiToJsonException, ParseException, org.json.simple.parser.ParseException, IOException {
        GameRecordDto gameRecordDto = null;
        gameRecordDto = gameRecordMachineLearningService.getGameRecord(lolName, count);
//        try {
//            gameRecordDto = gameRecordService.getGameRecord(lolName);
//        } catch (Exception e) {
//            throw new LolApiToJsonException("롤 api에 호출시 에러");
//        }
        return new ResponseEntity(gameRecordDto, HttpStatus.OK);
    }

}

