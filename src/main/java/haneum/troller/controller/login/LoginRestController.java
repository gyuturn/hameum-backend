package haneum.troller.controller.login;

import haneum.troller.domain.Member;
import haneum.troller.dto.LoginForm;
import haneum.troller.dto.SignUpForm;
import haneum.troller.security.SecurityService;
import haneum.troller.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LoginRestController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;


    @PostMapping("signUp")
    public Member signUp( SignUpForm signUpForm) {

        Member member = new Member();
        member.setEMail(signUpForm.getEMail());
        String password = signUpForm.getPassword();
        String encode = passwordEncoder.encode(password);
        member.setPassword(encode);
        member.setLolName(signUpForm.getLolName());

        memberService.join(member);

        return member;
    }

    @PostMapping("login")
    public boolean login(@RequestParam(value = "eMail") String eMail, @RequestParam(value="password") String password,
                         HttpServletResponse response) {
        LoginForm loginForm = new LoginForm(eMail, password);
        boolean checkLogin = memberService.validLogin(loginForm);
        if(!checkLogin) return false; //비밀번호가 틀린 경우

        String token=securityService.createToken(eMail,2*1000*60); //토큰 주기 2분으로 설정 (test)
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("result", token);
        Cookie idCookie = new Cookie("loginToken", token);
        idCookie.setMaxAge(60*2); //쿠키 세션 만료 2분 test
        response.addCookie(idCookie);

        return true;//최종적으로 로그인 성공
    }

}
