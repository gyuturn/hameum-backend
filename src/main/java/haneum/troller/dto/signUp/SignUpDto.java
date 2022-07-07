package haneum.troller.dto.signUp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class SignUpDto {

    private String email;

    private String password;

    private String lolName;


    @Override
    public String toString() {
        return "SignUpForm{" +
                "eMail='" + email + '\'' +
                ", password='" + password + '\'' +
                ", lolName='" + lolName + '\'' +
                '}';
    }
}
