package haneum.troller.controller.login;

import haneum.troller.domain.Member;
import haneum.troller.dto.LoginForm;
import haneum.troller.dto.SignUpForm;
import haneum.troller.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginRestController {
    private final MemberService memberService;


    @PostMapping("signUp")
    public Member signUp( SignUpForm signUpForm) {

        Member member = new Member();
        member.setEMail(signUpForm.getEMail());
        member.setPassword(signUpForm.getPassword());
        member.setLolName(signUpForm.getLolName());

        memberService.join(member);

        return member;
    }

    @PostMapping("login")
    public boolean login(LoginForm loginForm) {

        boolean checkLogin = memberService.validLogin(loginForm);

        if(!checkLogin) return true;
        else return false;
    }

}
