package haneum.troller.controller.signUp;

import haneum.troller.domain.Member;
import haneum.troller.dto.signUp.*;
import haneum.troller.repository.MemberRepository;
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
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Tag(name="SignUp",description = "회원가입API")
@RequestMapping("/api/member/sign-up/")
@RestController
@RequiredArgsConstructor
@Slf4j
public class SignUpRestController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MyPageService myPageService;
    private final EmailServiceImpl emailService;


    @Operation(summary = "멤버 등록 api",description = "모든 인증절차를 마치고 최종적으로 회원을 등록하는 기능" +
            "http status:201이 정상 등록")
    @PostMapping("register")
    @ApiResponses({
            @ApiResponse(responseCode = "201",description = "CREATED"),
            @ApiResponse(responseCode = "400",description = "서버에서 해당 parameter를 binding하지 못함" +
                    "오타 체크")
    })
    @Parameters(
            {
                    @Parameter(name = "email",description = "이메일"),
                    @Parameter(name="password",description = "비번"),
                    @Parameter(name="lolName",description = "롤 이름")
            }
    )
    public ResponseEntity<Member> signUp( @RequestBody SignUpDto signUpDto) {
        String encode = passwordEncoder.encode(signUpDto.getPassword());
        Member member = Member.builder()
                .email(signUpDto.getEmail())
                .password(encode)
                .lolName(signUpDto.getLolName())
                .build();

        memberRepository.save(member);
        log.info("회원등록");
        log.debug("등록된 회원:{}",member.toString());
        return new ResponseEntity(member, HttpStatus.CREATED);
    }




    @Operation(summary = "이메일인증 api",description = "회원가입시에 사용되는 이메일인증 api")
    @Parameters(
            {
                    @Parameter(name = "email",description = "이메일"),
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "OK"),
            @ApiResponse(responseCode = "400",description = "서버에서 해당 parameter를 binding하지 못함" +
                    "오타 체크"),
            @ApiResponse(responseCode = "403",description = "메시지 코드 보내는 도중 오류")
    })
    @GetMapping("email/send/code")
    public ResponseEntity emailConfirm(@RequestParam String email){
        try {
            emailService.sendSimpleMessage(email);
        } catch (Exception e) {
            log.info("이메일 인증코드 보내는 중 오류 발생");
            log.debug("해당 오류:{}",e.toString());
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        log.info("이메일 인증코드 발송");
        return new ResponseEntity(HttpStatus.OK);
    }


    @Operation(summary = "이메일인증코드 api",description = "회원가입시에 이메일인증 후 인증코드 확인하는 기능을 함, 프론트단에서 validTime을 받음")
    @Parameters(
            {
                    @Parameter(name = "code",description = "8자리 글자의 이메일로 받은 인증코드"),
                    @Parameter(name = "validTime",description = "Ex)3분->180")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "OK"),
            @ApiResponse(responseCode = "400",description = "서버에서 해당 parameter를 binding하지 못함" +
                    "오타 체크"),
            @ApiResponse(responseCode = "401",description = "인증코드가 서버단에서 허용되지 않음")
    })
    @PostMapping("email/auth/code")
    public ResponseEntity verifyCode(@RequestBody VerifyCodeDto verifyCodeDto) {
        int time = Integer.parseInt(verifyCodeDto.getValidTime());
        if(time<=0) {
            log.info("인증코드 시간초과");
            log.debug("해당 시간:{}",time);
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        if (!emailService.getEPw().equals(verifyCodeDto.getCode())) {
            log.info("인증코드 불일치");
            log.debug("요청한 인증코드:{}",verifyCodeDto.getCode());
            log.debug("실제 인증코드:{}",emailService.getEPw());
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        log.info("이메일 인증코드가 일치함");
        log.debug("인증코드:{}",verifyCodeDto.getCode());
        return new ResponseEntity(HttpStatus.OK);
    }

    @Operation(summary = "이미 등록된 이메일 검증 api",description = "회원가입시에 이메일이 이미 db에 등록되어 있는지 검사하는 기능을 함")
    @Parameters(
            {
                    @Parameter(name = "email",description = "이메일")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "OK"),
            @ApiResponse(responseCode = "400",description = "서버에서 해당 parameter를 binding하지 못함" +
                    "오타 체크"),
            @ApiResponse(responseCode = "403",description = "해당 이메일은 이미 사용중(중복)")
    })
    @GetMapping("/email/duplicate")
    public ResponseEntity checkDupEmail(@RequestParam String email){
        log.info("이메일 중복확인");
        if(memberService.checkDuplicateEmail(email)){
            log.debug("사용중인 이메일:{}",email);
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }else{
            log.debug("이메일 사용가능:{}",email);
            return new ResponseEntity(HttpStatus.OK);
        }
    }



    //롤 닉네임 있는지 validateCheck
    @Operation(summary = "롤 닉네임 유효성 검사api",description = "롤 닉네임이 실제 존재하는지, db에는 아직 등록되어 있지 않은지 검사" +
            "true값이어야 정상적으로 사용가능")
    @Parameters(
            {
                    @Parameter(name = "lolName",description = "롤 이름(띄어쓰기도 고려됨)")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "OK-정상적으로 조회됨->판단은 body보고 판단"),
            @ApiResponse(responseCode = "400",description = "서버에서 해당 parameter를 binding하지 못함" +
                    "오타 체크"),
            @ApiResponse(responseCode = "403",description = "해당 롤닉네임 이미 사용중(중복)")
    })
    @GetMapping("/check/lol-name")
    public ResponseEntity checkLoLName(@RequestParam("lolName") String lolName) throws ParseException {
        log.info("롤 닉네임 유효성 검사");
        CheckLoLNameDto checkLoLNameDto = new CheckLoLNameDto();
        checkLoLNameDto.setDupLolName(memberService.checkDuplicateLolName(lolName)); //이미 등록되어 있는 롤 닉네임
        checkLoLNameDto.setValidLolName(myPageService.checkLolName(lolName)); //롤 게임상 유효하지 않은 롤 닉네임
        return new ResponseEntity(checkLoLNameDto,HttpStatus.OK);
    }

}
