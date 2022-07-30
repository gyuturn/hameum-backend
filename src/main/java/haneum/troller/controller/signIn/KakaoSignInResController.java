package haneum.troller.controller.signIn;


import haneum.troller.common.security.JwtEncoder;
import haneum.troller.domain.Member;
import haneum.troller.dto.jwtDto.JwtDto;
import haneum.troller.dto.login.KakaoLoginDto;
import haneum.troller.repository.MemberRepository;
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
    private final JwtEncoder jwtEncoder;

    @Operation(summary = "카카오 accessToken api", description = "카카오에서 받은 accesstoken을 서버쪽으로 전달하기 위한 api'\n" +
            "이후 status code 추가 예정"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공+jwt 토큰 발급")
            }
    )
    @PostMapping("/access-token")
    public ResponseEntity LoginKakao(@RequestBody KakaoLoginDto kakaoLoginDto) {
        Long id = memberService.kakaoJoin(kakaoLoginDto);
        Member member = memberRepository.findById(id).get();
        JwtDto jwtDto = jwtEncoder.makeTokensForLogin(member);
        return new ResponseEntity(jwtDto, HttpStatus.OK);
    }

}
