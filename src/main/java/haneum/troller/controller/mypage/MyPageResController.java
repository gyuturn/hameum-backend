package haneum.troller.controller.mypage;

import haneum.troller.common.security.JwtEncoder;
import haneum.troller.dto.myPage.MyPageDto;
import haneum.troller.repository.MemberRepository;
import haneum.troller.service.MyPageService;
import io.jsonwebtoken.ExpiredJwtException;
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

@Tag(name="MyPage",description = "MyPage 조회시 사옹되는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage/")
@Slf4j
public class MyPageResController {
    private final MyPageService myPageService;
    private final JwtEncoder jwtEncoder;
    private final MemberRepository memberRepository;


    @Operation(summary = "유저의 간략한정보 api", description = "마이페이지에서 간략한 유저 정보를 알려주는 기능'\n" +
            "Header:Access-Token'\n")
    @Parameters(
            {
                    @Parameter(name = "lolName", description = "롤 닉네임")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "정상적 조회"),
                    @ApiResponse(responseCode = "403", description = "access-token 만료"),
                    @ApiResponse(responseCode = "403", description = "유저(롤 닉네임)정보가 롤에 없음->이름이 변경됐거나, 회원가입시 인증 로직 에러")
            }
    )
    @GetMapping("user/info")
    public ResponseEntity getTokenForMyPage(@RequestHeader("Access-Token") String accessToken) {
        String Id=null;
        try {
            Id = jwtEncoder.getSubjectByToken(accessToken);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
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