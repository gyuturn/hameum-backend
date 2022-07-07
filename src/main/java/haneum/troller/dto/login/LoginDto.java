package haneum.troller.dto.login;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class LoginDto {
    private String eMail;

    private String password;

    public LoginDto(String eMail, String password) {
        this.eMail = eMail;
        this.password = password;
    }
}
