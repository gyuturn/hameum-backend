package haneum.troller.controller.mypage;

import haneum.troller.common.aop.annotation.Auth;
import haneum.troller.service.security.JwtService;
import haneum.troller.dto.myPage.MyPageDto;
import haneum.troller.repository.MemberRepository;
import haneum.troller.service.mypage.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="MyPage",description = "MyPage 조회시 사옹되는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage/")
@Slf4j
public class MyPageResController {
    private final MyPageService myPageService;
    private final JwtService jwtService;
    private final MemberRepository memberRepository;


    @Operation(summary = "유저의 간략한정보 api", description = "마이페이지에서 간략한 유저 정보를 알려주는 기능'\n" +
            "Header:JWT-accessToken'\n")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "정상적 조회"),
                    @ApiResponse(responseCode = "403", description = "access-token 만료"),
            }
    )
    @GetMapping("user/info")
    @Auth
    public ResponseEntity getTokenForMyPage(@RequestHeader("JWT-accessToken") String accessToken ) {
        String Id=null;

        Id = jwtService.getSubjectByToken(accessToken);

        String lolName = memberRepository.findById(Long.valueOf(Id)).get().getLolName();

        MyPageDto myPageDtoFinal = null;
        try {
            log.info("유저 정보 조회");
            MyPageDto myPageDto = myPageService.getEncryptedLolName(lolName); //level,icon,name 저장
            myPageDtoFinal = myPageService.getMyPageAttr(myPageDto);
        } catch (Exception e) {
            log.debug("유저 정보 조회 실패");
            log.debug("{}", e.toString());
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(myPageDtoFinal, HttpStatus.OK);
    }
}