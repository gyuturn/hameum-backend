package haneum.troller.dto.login;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class SignInDto {
    private String email;

    private String password;

    public SignInDto(String eMail, String password) {
        this.email = eMail;
        this.password = password;
    }
}
