package haneum.troller.controller.fullSearch;

import haneum.troller.common.exception.exceptions.LolApiToJsonException;
import haneum.troller.dto.gameRecord.GameRecordDto;
import haneum.troller.dto.linePrefer.LinePreferenceDto;
import haneum.troller.dto.mostChampion.MostThreeChampionDto;
import haneum.troller.dto.myPage.MyPageDto;
import haneum.troller.service.GameRecordService;
import haneum.troller.service.LinePreferenceService;
import haneum.troller.service.MostThreeChampionService;
import haneum.troller.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;

@Tag(name="userGameRecord",description = "유저의 게임전적 조회시 사옹되는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search/")
@Slf4j
public class FullSearchController {
    private final MyPageService myPageService;
    private final LinePreferenceService linePreferenceService;
    private final GameRecordService gameRecordService;
    private final MostThreeChampionService mostThreeChampionService;

    @Operation(summary = "유저의 간략한정보 api", description = "전적검색/마이페이지에서 간략한 유저 정보를 알려주는 기능")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",description = "정상적 조회"),
                    @ApiResponse(responseCode = "404",description = "해당 유저가 없음")
            }
    )
    @GetMapping("user/info")
    public ResponseEntity getTokenForMyPage(@RequestParam(value = "lolName") String lolName) {
        MyPageDto myPageDtoFinal = null;
        try {
            log.info("유저 정보 조회");
            MyPageDto myPageDto = myPageService.getEncryptedLolName(lolName); //level,icon,name 저장
            myPageDtoFinal = myPageService.getMyPageAttr(myPageDto);
        } catch (Exception e) {
            log.debug("유저 정보 조회 실패");
            log.debug("{}",e.toString());
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(myPageDtoFinal,HttpStatus.OK);
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
    public ResponseEntity getTokenForLine(@RequestParam(value = "lolName") String lolName) throws LolApiToJsonException {
        LinePreferenceDto linePreferenceDto = null;
        try {
            linePreferenceDto = linePreferenceService.getLinePreferenceDto(lolName);
        } catch (Exception e) {
            throw new LolApiToJsonException("롤 api에 호출시 에러");
        }
        return new ResponseEntity(linePreferenceDto, HttpStatus.OK);
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
    @GetMapping("usre/most")
    public ResponseEntity getTokenForMost(@RequestParam(value = "lolName") String lolName) throws LolApiToJsonException {
        MostThreeChampionDto mostThreeChampionDto = null;
        try{
            mostThreeChampionDto = mostThreeChampionService.getMostThreeChampionDto(lolName);
        } catch (Exception e) {
            throw new LolApiToJsonException("롤 api에 호출시 에러");
        }
        return new ResponseEntity(mostThreeChampionDto, HttpStatus.OK);
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
    public ResponseEntity getTokenForGameRecord(@RequestParam(value = "lolName") String lolName) throws LolApiToJsonException {
        GameRecordDto gameRecordDto = null;
        try {
            gameRecordDto = gameRecordService.getGameRecord(lolName);
        } catch (Exception e) {
            throw new LolApiToJsonException("롤 api에 호출시 에러");
        }
        return new ResponseEntity(gameRecordDto, HttpStatus.OK);
    }

}
