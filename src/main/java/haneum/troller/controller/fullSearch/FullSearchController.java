package haneum.troller.controller.fullSearch;

import haneum.troller.dto.myPage.MyPageDto;
import haneum.troller.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="userGameRecord",description = "유저의 게임전적 조회시 사옹되는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search/")
@Slf4j
public class FullSearchController {
    private final MyPageService myPageService;

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


}
