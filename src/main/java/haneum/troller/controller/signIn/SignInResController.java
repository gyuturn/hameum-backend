package haneum.troller.controller.signIn;

import haneum.troller.domain.Member;
import haneum.troller.dto.jwtDto.JwtDto;
import haneum.troller.dto.login.SignInDto;
import haneum.troller.repository.MemberRepository;
import haneum.troller.security.SecurityService;
import haneum.troller.service.EmailServiceImpl;
import haneum.troller.service.MemberService;
import haneum.troller.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="signIn",description = "로그인 API")
@RequestMapping("/member/sign-in/")
@RestController
@RequiredArgsConstructor
public class SignInResController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final SecurityService securityService;


    @Operation(summary = "로그인 api",description = "로그인시에 사용되는 api" +
            "로그인 성공시에는 200 status와 토큰 두개 얻음 바디로 얻음" +
            "실패시에는 401 status"

    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",description = "로그인 성공+jwt 토큰 방급"),
                    @ApiResponse(responseCode = "401",description = "로그인 실패"),
                    @ApiResponse(responseCode = "400",description = "서버에서 해당 parameter를 binding하지 못함" +
                            "오타 체크")
            }
    )
    @Parameters(
            {
                    @Parameter(name = "email",description = "이메일"),
                    @Parameter(name="password",description = "비번")
            }
    )
    @PostMapping("/in")
    public ResponseEntity<JwtDto> login(@RequestBody SignInDto signInDto) {
        SignInDto loginDto = new SignInDto(signInDto.getEmail(), signInDto.getPassword());
        boolean checkLogin = memberService.validLogin(loginDto);
        if (checkLogin) {
            Member member = memberRepository.findByEmail(loginDto.getEmail());

            String accessToken = securityService.createToken(member.getEmail(), 60 * 1000 * 60); //토큰 주기 1시간으로 설정 (test)
            String refreshToken = securityService.createToken(member.getEmail(), 60 * 1000 * 60 * 24 * 7); //토큰 주기 1주일으로 설정 (test)

            System.out.println("accessToken = " + accessToken);
            System.out.println("refreshToken = " + refreshToken);
            JwtDto jwtDto = JwtDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
            return new ResponseEntity(jwtDto, HttpStatus.OK);
        }
        else{
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

    }
}
