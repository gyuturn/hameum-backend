package haneum.troller.controller.signIn;


import haneum.troller.common.security.JwtEncoder;
import haneum.troller.domain.Member;
import haneum.troller.dto.jwtDto.JwtDto;
import haneum.troller.dto.kakaoDto.AuthorizationDto;
import haneum.troller.dto.kakaoDto.KakaoSignUpDto;
import haneum.troller.repository.MemberRepository;
import haneum.troller.service.login.KaKaoLoginService;
import haneum.troller.service.login.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="signIn for kako",description = "kakao 로그인 API")
@RequestMapping("/api/member/sign-in/kakao")
@RestController
@Slf4j
@RequiredArgsConstructor
public class KakaoSignInResController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final KaKaoLoginService kaKaoLoginService;
    private final JwtEncoder jwtEncoder;

    @Operation(summary = "카카오 회원가입 api", description = "카카오 로그인 이후 회원가입 등록"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "회원가입 성공+jwt 토큰 발급"),
                    @ApiResponse(responseCode = "400", description = "카카오 로그인시 인증 오류")
            }
    )
    @PostMapping("/sign-up")
    public ResponseEntity signUpKakao(@RequestBody KakaoSignUpDto kakaoSignUpDto) throws Exception {
        Member member = memberService.kakaoJoin(kakaoSignUpDto);
        JwtDto jwtDto = jwtEncoder.makeTokensForLogin(member);
        return new ResponseEntity(jwtDto, HttpStatus.OK);
    }


    @Operation(summary = "카카오 로그인 api", description = "카카오에서 받은 code(인가코드) 서버쪽으로 전달하기 위한 api"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공+jwt 토큰 발급"),
                    @ApiResponse(responseCode = "400", description = "카카오 로그인시 인증 오류"),
                    @ApiResponse(responseCode = "401",description = "카카오 회원가입 필요")
            }
    )
    @PostMapping("/login")
    public ResponseEntity LoginKakao(@RequestBody AuthorizationDto authorizationDto) throws Exception {
        JwtDto kakaoJwt = kaKaoLoginService.getKakaoAccessToken(authorizationDto.getCode());
        String email = kaKaoLoginService.getEmailByAccessToken(kakaoJwt.getAccessToken());
        //이미 회원가입이 되어 있는 경우
        if(!memberService.checkDuplicateEmail(email)){
            Member member = memberRepository.findByEmail(email);
            JwtDto jwtDto = jwtEncoder.makeTokensForLogin(member);
            return new ResponseEntity(jwtDto, HttpStatus.OK);
        }
        else{
            return new ResponseEntity( HttpStatus.UNAUTHORIZED);
        }
    }

}
