package haneum.troller.controller.login;

import haneum.troller.domain.Member;
import haneum.troller.dto.signUp.*;
import haneum.troller.dto.login.LoginDto;
import haneum.troller.security.SecurityService;
import haneum.troller.service.EmailServiceImpl;
import haneum.troller.service.MemberService;
import haneum.troller.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LoginRestController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;
    private final MyPageService myPageService;
    private final EmailServiceImpl emailService;


    @PostMapping("sign_up")
    public Member signUp(HttpServletRequest request, @RequestBody SignUpDto signUpDto) {
        System.out.println(request);

        Member member = new Member();
        member.setEMail(signUpDto.getEmail());
        String encode = passwordEncoder.encode(signUpDto.getPassword());
        member.setPassword(encode);
        member.setLolName(signUpDto.getLolName());

        memberService.join(member);

        return member;
    }

    @PostMapping("sign_in")
    public boolean login(@RequestBody SignUpDto signUpDto,
                         HttpServletResponse response) {
        LoginDto loginDto = new LoginDto(signUpDto.getEmail(), signUpDto.getPassword());
        try {
            boolean checkLogin = memberService.validPassword(loginDto);
            if (!checkLogin || !memberService.checkDuplicateEmail(signUpDto.getEmail())) return false; //비밀번호 or 아이디가 틀린 경우
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }
        finally {

        }


        String token=securityService.createToken(signUpDto.getEmail(),20*1000*60); //토큰 주기 2분으로 설정 (test)
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("result", token);
        Cookie idCookie = new Cookie("loginToken", token);
        idCookie.setMaxAge(60*20); //쿠키 세션 만료 2분 test
        response.addCookie(idCookie);

        return true;//최종적으로 로그인 성공
    }



    @PostMapping("/email_auth")
    public boolean emailConfirm(@RequestBody EmailAuthDto emailAuthDto)throws Exception{
        emailService.sendSimpleMessage(emailAuthDto.getEmail());
        return true;
    }

    @PostMapping("/verify_code")
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
    @PostMapping("/check_dup_email")
    public boolean checkDupEmail(@RequestBody DupEmailDto dupEmailDto){
        return memberService.checkDuplicateEmail(dupEmailDto.getEmail());
    }

    //롤 닉네임 있는지 validateCheck
    @PostMapping("/check_lol_name")
    public CheckLoLNameDto checkLoLName(@RequestBody CheckLoLNameDto checkLoLNameDto) throws ParseException {
        checkLoLNameDto.setDupLolName(memberService.checkDuplicateLolName(checkLoLNameDto.getLolName())); //이미 등록되어 있는 롤 닉네임
        checkLoLNameDto.setValidLolName(myPageService.checkLolName(checkLoLNameDto.getLolName())); //롤 게임상 유효하지 않은 롤 닉네임
        return checkLoLNameDto;
    }

}
