package haneum.troller.controller.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {


    @GetMapping("/")
    public String mainPage() {
        return "main";
    }

    @GetMapping("signUp")
    public String signUp() {
        return "member/signUp";
    }
    @GetMapping("login")
    public String login() {
        return "member/login";
    }
}
