package haneum.troller.controller.login;

import haneum.troller.domain.Member;
import haneum.troller.dto.member.CheckLoLNameDto;
import haneum.troller.dto.member.LoginForm;
import haneum.troller.dto.member.SignUpForm;
import haneum.troller.security.SecurityService;
import haneum.troller.service.EmailServiceImpl;
import haneum.troller.service.MemberService;
import haneum.troller.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private final EmailServiceImpl emailService;
    private final MyPageService myPageService;


    @PostMapping("sign_up")
    public Member signUp(HttpServletRequest request, @RequestParam("eMail")String eMail, @RequestParam("password")String password, @RequestParam("lolName")String lolName) {
        System.out.println(request);

        Member member = new Member();
        member.setEMail(eMail);
        String encode = passwordEncoder.encode(password);
        member.setPassword(encode);
        member.setLolName(lolName);

        memberService.join(member);

        return member;
    }

    @PostMapping("sign_in")
    public boolean login(@RequestParam(value = "eMail") String eMail, @RequestParam(value="password") String password,
                         HttpServletResponse response) {
        LoginForm loginForm = new LoginForm(eMail, password);
        try {
            boolean checkLogin = memberService.validPassword(loginForm);
            if (!checkLogin || !memberService.checkDuplicateEmail(eMail)) return false; //비밀번호 or 아이디가 틀린 경우
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }
        finally {

        }


        String token=securityService.createToken(eMail,20*1000*60); //토큰 주기 2분으로 설정 (test)
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("result", token);
        Cookie idCookie = new Cookie("loginToken", token);
        idCookie.setMaxAge(60*20); //쿠키 세션 만료 2분 test
        response.addCookie(idCookie);

        return true;//최종적으로 로그인 성공
    }


    @PostMapping("/mail_auth")
    public void emailConfirm(@RequestParam("userId")String userId)throws Exception{
        System.out.println("전달 받은 이메일 : "+userId);
        emailService.sendSimpleMessage(userId);
    }

    @PostMapping("/verify_code")
    public boolean verifyCode(@RequestParam(value = "code") String code,@RequestParam(value = "expiredTime") String expiredTime) {
        boolean result = false;
        int time = Integer.parseInt(expiredTime);
        if(time>180) return false;
        if(EmailServiceImpl.ePw.equals(code)) {
            result =true;
        }

        return result;
    }

    //중복이메일 검증 true=>중복된 이메일 없음
    @PostMapping("/check_dup_email")
    public boolean checkDupEmail(@RequestParam("email") String email){
        return memberService.checkDuplicateEmail(email);
    }

    //롤 닉네임 있는지 validateCheck
    @PostMapping("/check_lol_name")
    public CheckLoLNameDto checkLoLName(@RequestParam("lolName") String lolName) throws ParseException {
        CheckLoLNameDto checkLoLNameDto = new CheckLoLNameDto();
        checkLoLNameDto.setDupLolName(memberService.checkDuplicateLolName(lolName)); //이미 등록되어 있는 롤 닉네임
        checkLoLNameDto.setValidLolName(myPageService.checkLolName(lolName)); //롤 게임상 유효하지 않은 롤 닉네임

        return checkLoLNameDto;
    }

}
