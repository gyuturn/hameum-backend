package haneum.troller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpForm {

    private String eMail;

    private String password;

    private String lolName;

    @Override
    public String toString() {
        return "SignUpForm{" +
                "eMail='" + eMail + '\'' +
                ", password='" + password + '\'' +
                ", lolName='" + lolName + '\'' +
                '}';
    }
}
