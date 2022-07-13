package haneum.troller.controller.fullSearch;

import haneum.troller.dto.myPage.MyPageDto;
import haneum.troller.security.SecurityService;
import haneum.troller.service.MyPageService;
import haneum.troller.service.dataDragon.MyPageImgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name="userInfo",description = "유저의 간략한 정보/티어그래프/마이페이지에 사용되는 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class FullSearchController {
    private final SecurityService securityService;
    private final MyPageService myPageService;
    private final MyPageImgService myPageImgService;

    @Operation(summary = "유저의 간략한정보 api",description = "전적검색/마이페이지에서 간략한 유저 정보를 알려주는 기능")
    @Parameters(
            {
                    @Parameter(name = "lolName",description = "롤 닉네임")
            }
    )
    @GetMapping("/user_info")
    public MyPageDto getTokenForMyPage(@RequestParam(value = "lolName") String lolName) throws IllegalAccessException, ParseException, IOException {
        MyPageDto myPageDto = myPageService.getEncryptedLolName(lolName); //level,icon,name 저장
        MyPageDto myPageDtoFinal = myPageService.getMyPageAttr(myPageDto);
        return myPageDtoFinal;
    }


}
