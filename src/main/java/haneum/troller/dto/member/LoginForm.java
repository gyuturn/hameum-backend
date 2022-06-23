package haneum.troller.dto.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginForm {
    private String eMail;

    private String password;

    public LoginForm(String eMail, String password) {
        this.eMail = eMail;
        this.password = password;
    }
}
