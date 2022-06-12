package haneum.troller.controller;

import haneum.troller.domain.Member;
import haneum.troller.form.SignUpForm;
import haneum.troller.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final MemberService memberService;


    @GetMapping("/")
    public String mainPage() {
        return "main";
    }

    @GetMapping("signUp")
    public String signUp(Model model) {
        model.addAttribute("signUpForm", new SignUpForm());
        return "member/signUp";
    }

    @PostMapping("signUp")
    public String signUp(@Validated @ModelAttribute SignUpForm signUpForm, BindingResult result) {
        if (result.hasErrors()) {
            return "error";
        }
        Member member = new Member();
        member.setEMail(signUpForm.getEMail());
        member.setPassword(signUpForm.getPassword());
        member.setLolName(signUpForm.getLolName());

        memberService.join(member);

        return "redirect:/";
    }
}
