package haneum.troller.controller.signIn;

import haneum.troller.domain.Member;
import haneum.troller.dto.jwtDto.JwtDto;
import haneum.troller.dto.login.SignInDto;
import haneum.troller.repository.MemberRepository;
import haneum.troller.service.security.JwtService;
import haneum.troller.service.login.MemberService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="signIn",description = "로그인 API")
@RequestMapping("/api/member/sign-in/")
@RestController
@Slf4j
@RequiredArgsConstructor
public class SignInResController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final JwtService jwtService;


    @Operation(summary = "로그인 api",description = "로그인시에 사용되는 api" +
            "로그인 성공시에는 200 status와 토큰 두개 얻음 바디로 얻음" +
            "실패시에는 401 status'\n" +
            "accessToken, refreshToken 저장''\n" +
            "refesh token은 db에 저장"

    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",description = "로그인 성공+jwt 토큰 방급"),
                    @ApiResponse(responseCode = "401",description = "로그인 실패"),
                    @ApiResponse(responseCode = "400",description = "서버에서 해당 parameter를 binding하지 못함" +
                            "오타 체크")
            }
    )
    @PostMapping("")
    public ResponseEntity<JwtDto> login(@RequestBody SignInDto signInDto) {
        Member member = memberRepository.findByEmail(signInDto.getEmail());
        if (memberService.validLogin(signInDto)&&memberService.findLoginType(member)) {
            return new ResponseEntity(jwtService.makeTokensForLogin(member), HttpStatus.OK);
        }
        else{
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

    }
}
