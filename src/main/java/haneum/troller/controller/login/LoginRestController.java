package haneum.troller.controller.login;

import haneum.troller.domain.Member;
import haneum.troller.dto.jwtDto.JwtDto;
import haneum.troller.dto.signUp.*;
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
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Tag(name="sign",description = "회원가입/로그인 API")
@RequestMapping("/sign")
@RestController
@RequiredArgsConstructor
public class LoginRestController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;
    private final MyPageService myPageService;
    private final EmailServiceImpl emailService;


    @Operation(summary = "회원가입 api",description = "최종적으로 회원가입시 회원을 db에 등록")
    @PostMapping("/up/register")
    @Parameters(
            {
                    @Parameter(name = "email",description = "이메일"),
                    @Parameter(name="password",description = "비번"),
                    @Parameter(name="lolName",description = "롤 이름")
            }
    )
    public Member signUp( @RequestBody SignUpDto signUpDto) {
        Member member = new Member();
        member.setEmail(signUpDto.getEmail());
        String encode = passwordEncoder.encode(signUpDto.getPassword());
        member.setPassword(encode);
        member.setLolName(signUpDto.getLolName());

        memberService.join(member);

        return member;
    }


    @Operation(summary = "로그인 api",description = "로그인시에 사용되는 api" +
            "로그인 성공시에는 200 status와 토큰 두개 얻음 바디로 얻음" +
            "실패시에는 401 status"

    )
    @ApiResponses(
        value = {
                @ApiResponse(responseCode = "200",description = "로그인 성공+jwt 토큰 방급"),
                @ApiResponse(responseCode = "401",description = "로그인 실패")
        }
    )
    @Parameters(
            {
                    @Parameter(name = "email",description = "이메일"),
                    @Parameter(name="password",description = "비번")
            }
    )
    @PostMapping("/in")
    public ResponseEntity login(@RequestBody SignInDto signInDto) {
        SignInDto loginDto = new SignInDto(signInDto.getEmail(), signInDto.getPassword());
        try {
            boolean checkLogin = memberService.validLogin(loginDto);
            if (checkLogin){
                Member member = memberRepository.findByEmail(loginDto.getEmail()).get(0);

                String accessToken = securityService.createToken(member.getLolName(), 60 * 1000 * 60); //토큰 주기 1시간으로 설정 (test)
                String refreshToken = securityService.createToken(member.getEmail(), 60 * 1000 * 60 * 24 * 7); //토큰 주기 1주일으로 설정 (test)

                System.out.println("accessToken = " + accessToken);
                System.out.println("refreshToken = " + refreshToken);
                JwtDto jwtDto = JwtDto.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                return new ResponseEntity(jwtDto, HttpStatus.OK);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        finally {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }



    }



    @Operation(summary = "이메일인증 api",description = "회원가입시에 사용되는 이메일인증 api")
    @Parameters(
            {
                    @Parameter(name = "email",description = "이메일"),
            }
    )
    @PostMapping("/up/email_auth")
    public boolean emailConfirm(@RequestBody EmailAuthDto emailAuthDto)throws Exception{
        emailService.sendSimpleMessage(emailAuthDto.getEmail());
        return true;
    }


    @Operation(summary = "이메일인증코드 api",description = "회원가입시에 이메일인증 후 인증코드 확인하는 기능을 함, 추가적으로 프론트단에서 validTime을 받음")
    @Parameters(
            {
                    @Parameter(name = "code",description = "8자리 글자의 이메일로 받은 인증코드"),
                    @Parameter(name = "validTime",description = "Ex)3분->180")
            }
    )
    @PostMapping("/up/verify_code")
    public boolean verifyCode(@RequestBody VerifyCodeDto verifyCodeDto) {
        boolean result = false;
        int time = Integer.parseInt(verifyCodeDto.getValidTime());
        if(time<=0) return false;
        if(emailService.getEPw().equals(verifyCodeDto.getCode())) {
            result =true;
        }

        return result;
    }

    //중복이메일 검증 true=>중복된 이메일 없음
    @Operation(summary = "이미 등록된 이메일 검증 api",description = "회원가입시에 이메일이 이미 db에 등록되어 있는지 검사하는 기능을 함")
    @Parameters(
            {
                    @Parameter(name = "email",description = "이메일")
            }
    )
    @PostMapping("/up/check/email")
    public boolean checkDupEmail(@RequestBody DupEmailDto dupEmailDto){
        return memberService.checkDuplicateEmail(dupEmailDto.getEmail());
    }



    //롤 닉네임 있는지 validateCheck
    @Operation(summary = "롤 닉네임 유효성 검사api",description = "롤 닉네임이 실제 존재하는지, db에는 아직 등록되어 있지 않은지 검사")
    @Parameters(
            {
                    @Parameter(name = "lolName",description = "롤 이름(띄어쓰기도 고려됨)")
            }
    )
    @GetMapping("/up/check/lol_name")
    public CheckLoLNameDto checkLoLName(@RequestParam("lolName") String lolName) throws ParseException {
        CheckLoLNameDto checkLoLNameDto = new CheckLoLNameDto();
        checkLoLNameDto.setDupLolName(memberService.checkDuplicateLolName(lolName)); //이미 등록되어 있는 롤 닉네임
        checkLoLNameDto.setValidLolName(myPageService.checkLolName(lolName)); //롤 게임상 유효하지 않은 롤 닉네임
        return checkLoLNameDto;
    }

}
